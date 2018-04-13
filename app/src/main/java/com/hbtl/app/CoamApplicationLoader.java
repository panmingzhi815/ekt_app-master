package com.hbtl.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.hbtl.api.CoamHttpService;
import com.hbtl.beans.AppDeviceInfo;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.models.CommonAccountInfo;
import com.hbtl.models.CommonAppRunInfo;
import com.hbtl.models.EktRealmMigration;
import com.hbtl.service.NetworkStateService;
import com.hbtl.service.RunningService;
import com.hbtl.utils.CoamDebugTree;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.lang.reflect.InvocationTargetException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

// 原来继承自 Application 但是,运行时老是出现未知错误,继承自 MultiDexApplication 便没有再出现类似问题
// http://www.mutualmobile.com/posts/dex-64k-limit-not-problem-anymore-almost
public class CoamApplicationLoader extends MultiDexApplication {

    // App 运行全局设备参数...
//    @Getter
//    @Setter
    public AppDeviceInfo appDeviceInfo;

    // 全局运行用户账户信息...
//    @Getter
//    @Setter
    public CommonAccountInfo appAccountInfo;

    // 全局运行App设置信息...
//    @Getter
//    @Setter
    public CommonAppRunInfo appRunInfo;

    // Other Setting...
    public static String appCacheDir = "";
    @Getter
    @Setter
    public static volatile Handler appHandler;
    @Getter
    @Setter
    public SharedPreferences appPreferences = null;

    // RealmDB ...
    RealmConfiguration appRealmConfig;
    Realm appRealmInstance;

    // Application 单例
    public static volatile CoamApplicationLoader appContextInstance = null;  // <<< 这里添加了 volatile

    public static CoamApplicationLoader getInstance() {
        CoamApplicationLoader inst = appContextInstance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (CoamApplicationLoader.class) {
                inst = appContextInstance;
                if (inst == null) {
                    inst = new CoamApplicationLoader();
                    appContextInstance = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
//            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
//            AndroidUtilities.checkDisplaySize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        // 首先初始化 mAppContext
        appContextInstance = this;// getApplicationContext()
        appHandler = new Handler(appContextInstance.getMainLooper());

        // 初始化变量
        appPreferences = PreferenceManager.getDefaultSharedPreferences(appContextInstance);

        // 初始化全局共享变量对象...
        appDeviceInfo = new AppDeviceInfo();
        appAccountInfo = new CommonAccountInfo();
        syncAccountPreferences("load");

        appRunInfo = new CommonAppRunInfo();
        syncRunPreferences("load");

        // 如果没有设置默认访问设置,则使用线上服务环境...
        if (appRunInfo.mpiServer == null) appRunInfo.mpiServer = CoamHttpService.MPI_SERVER;
        syncRunPreferences("save");

        // Realm Tester
        Realm.init(appContextInstance);
        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        appRealmConfig = new RealmConfiguration.Builder()
                .name("ekt.realm")
                .schemaVersion(BuildConfig.VERSION_CODE) // 在schema改变后,必须进行升级
                .migration(new EktRealmMigration()) // 开始迁移
                //.deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(appRealmConfig);

        // Get a Realm instance for this thread
        //appRealmInstance = Realm.getInstance(appRealmConfig);

        // 开发模式下启用 Timber 调试支持
        if (BuildConfig.DEBUG) {
            //Timber.plant(new Timber.DebugTree());
            Timber.plant(new CoamDebugTree());
        } else {
            //TODO plant your Production Tree
            //Timber.plant(new CoamDebugTree());
        }

        // 收集未捕捉异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        // Create an InitializerBuilder
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        // 添加 stetho 调试 realm 支持 https://github.com/uPhyca/stetho-realm
//        initializerBuilder.enableWebKitInspector(
//                RealmInspectorModulesProvider.builder(this)
//                        .withFolder(getCacheDir())
//                        .withMetaTables()
//                        .withDescendingOrder()
//                        .build()
//        );//.build();
        // Enable command line interface
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        // Use the InitializerBuilder to generate an Initializer
        Stetho.Initializer initializer = initializerBuilder.build();
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer);

        // 升级 Iconify1.xx 到 Iconify2.xx
        // https://github.com/JoanZapata/android-iconify/blob/master/MIGRATION.md
        Iconify
                .with(new FontAwesomeModule());
//        .with(new EntypoModule());
//        .with(new TypiconsModule());
//        .with(new MaterialModule())
//        .with(new MeteoconsModule())
//        .with(new WeathericonsModule())
//        .with(new SimpleLineIconsModule())
//        .with(new IoniconsModule());

        // 获取当前 App Sha1 签名...
        //String mAppSha1 = CommonUtils.getApkSHA1(this);
        //Timber.i("|#|CoamApplicationLoader|Sign mAppSha1|" + mAppSha1);

        // 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            appCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            appCacheDir = getApplicationContext().getCacheDir().toString();
        }

        // 启动后台定时任务...
        Intent intent = new Intent(appContextInstance, RunningService.class);
        startService(intent);

        // 网络监听
        Intent nwsIntent = new Intent(appContextInstance, NetworkStateService.class);
        startService(nwsIntent);
    }

    // [way:save|load]
    public synchronized void syncAppPreferences(String way) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    }

    // [syncWay:save|load]
    public synchronized void syncAccountPreferences(String syncWay) {
        String appAccountPreferencesTag = "appAccountInfo_";
        if ("save".equals(syncWay)) {
            // asId...
            if (appAccountInfo.asId != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "asId", appAccountInfo.asId).apply();

            // accountName...
            if (appAccountInfo.accountName != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "accountName", appAccountInfo.accountName).apply();
            // password...
            if (appAccountInfo.password != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "password", appAccountInfo.password).apply();

            // aInfoName...
            if (appAccountInfo.aInfoName != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "aInfoName", appAccountInfo.aInfoName).apply();
            // aInfoPicture...
            if (appAccountInfo.aInfoPicture != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "aInfoPicture", appAccountInfo.aInfoPicture).apply();
            // aInfoPhone...
            if (appAccountInfo.aInfoPhone != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "aInfoPhone", appAccountInfo.aInfoPhone).apply();
            // aInfoEmail...
            if (appAccountInfo.aInfoEmail != null)
                appPreferences.edit().putString(appAccountPreferencesTag + "_" + "aInfoEmail", appAccountInfo.aInfoEmail).apply();
        } else if ("load".equals(syncWay)) {
            // asId...
            String asId = appPreferences.getString(appAccountPreferencesTag + "_" + "asId", null);
            if (asId != null)
                appAccountInfo.asId = asId;

            // accountName...
            String accountName = appPreferences.getString(appAccountPreferencesTag + "_" + "accountName", null);
            if (accountName != null)
                appAccountInfo.accountName = accountName;
            // password...
            String password = appPreferences.getString(appAccountPreferencesTag + "_" + "password", null);
            if (password != null)
                appAccountInfo.password = password;

            // aInfoName...
            String aInfoName = appPreferences.getString(appAccountPreferencesTag + "_" + "aInfoName", null);
            if (aInfoName != null)
                appAccountInfo.aInfoName = aInfoName;
            // aInfoPicture...
            String aInfoPicture = appPreferences.getString(appAccountPreferencesTag + "_" + "aInfoPicture", null);
            if (aInfoPicture != null)
                appAccountInfo.aInfoPicture = aInfoPicture;
            // aInfoPhone...
            String aInfoPhone = appPreferences.getString(appAccountPreferencesTag + "_" + "aInfoPhone", null);
            if (aInfoPhone != null)
                appAccountInfo.aInfoPhone = aInfoPhone;
            // aInfoEmail...
            String aInfoEmail = appPreferences.getString(appAccountPreferencesTag + "_" + "aInfoEmail", null);
            if (aInfoEmail != null)
                appAccountInfo.aInfoEmail = aInfoEmail;
        }
    }

    // [syncWay:save|load]
    public synchronized void syncRunPreferences(String syncWay) {
        String appRunPreferencesTag = "appRunInfo_";
        if ("save".equals(syncWay)) {
            // arId...
            if (appRunInfo.arId != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "arId", appRunInfo.arId).apply();

            // deviceId...
            if (appRunInfo.deviceId != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "deviceId", appRunInfo.deviceId).apply();

            // oUpDn...
            if (appRunInfo.oUpDn != 0)
                appPreferences.edit().putInt(appRunPreferencesTag + "_" + "oUpDn", appRunInfo.oUpDn).apply();

            // token...
            if (appRunInfo.token != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "token", appRunInfo.token).apply();

            // tokenEts...
            if (appRunInfo.tokenEts != 0)
                appPreferences.edit().putLong(appRunPreferencesTag + "_" + "tokenEts", appRunInfo.tokenEts).apply();

            // aesKey...
            if (appRunInfo.aesKey != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "aesKey", appRunInfo.aesKey).apply();

            // aesIv...
            if (appRunInfo.aesIv != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "aesIv", appRunInfo.aesIv).apply();

            // rsaPublicKey...
            if (appRunInfo.rsaPublicKey != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "rsaPublicKey", appRunInfo.rsaPublicKey).apply();

            // mpiServer...
            if (appRunInfo.mpiServer != null)
                appPreferences.edit().putString(appRunPreferencesTag + "_" + "mpiServer", appRunInfo.mpiServer).apply();
        } else if ("load".equals(syncWay)) {
            // arId...
            String arId = appPreferences.getString(appRunPreferencesTag + "_" + "arId", null);
            if (arId != null)
                appRunInfo.arId = arId;

            // deviceId...
            String deviceId = appPreferences.getString(appRunPreferencesTag + "_" + "deviceId", null);
            if (deviceId != null)
                appRunInfo.deviceId = deviceId;

            // oUpDn...
            int oUpDn = appPreferences.getInt(appRunPreferencesTag + "_" + "oUpDn", 1);
            if (oUpDn != 0)
                appRunInfo.oUpDn = oUpDn;

            // token...
            String token = appPreferences.getString(appRunPreferencesTag + "_" + "token", null);
            if (token != null)
                appRunInfo.token = token;

            // tokenEts...
            long tokenEts = appPreferences.getLong(appRunPreferencesTag + "_" + "tokenEts", 0);
            if (tokenEts != 0)
                appRunInfo.tokenEts = tokenEts;

            // aesKey...
            String aesKey = appPreferences.getString(appRunPreferencesTag + "_" + "aesKey", null);
            if (aesKey != null)
                appRunInfo.aesKey = aesKey;

            // aesIv...
            String aesIv = appPreferences.getString(appRunPreferencesTag + "_" + "aesIv", null);
            if (aesKey != null)
                appRunInfo.aesIv = aesIv;

            // rsaPublicKey...
            String rsaPublicKey = appPreferences.getString(appRunPreferencesTag + "_" + "rsaPublicKey", null);
            if (rsaPublicKey != null)
                appRunInfo.rsaPublicKey = rsaPublicKey;

            // mpiServer...
            String mpiServer = appPreferences.getString(appRunPreferencesTag + "_" + "mpiServer", null);
            if (mpiServer != null)
                appRunInfo.mpiServer = mpiServer;
        }
    }

    /**
     * 退出登录,清空数据
     */
    public void logout() {
        // 先调用sdk logout,在清理app中自己的数据
        //DbOpenHelper.getInstance(appContextInstance).closeDB();
        // reset password to null
        appAccountInfo.password = null;
        appAccountInfo.ifLogin = false;
        syncAccountPreferences("save");
    }

    public boolean isSelf(String asId) {
        if (appAccountInfo != null && appAccountInfo.asId.equals(asId)) {
            return true;
        }
        return false;
    }

    // https://github.com/yangfuhai/ASimpleCache/blob/master/source/src/org/afinal/simplecache/ACache.java
    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
