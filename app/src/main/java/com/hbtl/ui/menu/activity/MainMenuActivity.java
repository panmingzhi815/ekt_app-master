package com.hbtl.ui.menu.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.hbtl.api.CoamHttpService;
import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseInner;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.CommonMyHsAreaInfo;
import com.hbtl.beans.CrossCommonAppFaceDetectorBus;
import com.hbtl.beans.CrossIcReaderInfoBus;
import com.hbtl.beans.CrossUpdateEnterBus;
import com.hbtl.beans.CrossUploaderAuthInfoBus;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.ekt.R;
import com.hbtl.models.EnterAuthModel;
import com.hbtl.service.DownloadReceiver;
import com.hbtl.service.NetworkStateService;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.app.activity.AppSettingActivity;
import com.hbtl.ui.app.activity.FaceDetectorActivity;
import com.hbtl.ui.app.activity.ScenicAuthActivity;
import com.hbtl.ui.common.activity.CommonSearchAreaLineListActivity;
import com.hbtl.utils.CommonUtils;
import com.hbtl.utils.EktCso;
import com.hbtl.utils.ImageUtils;
import com.hbtl.utils.Md5Utils;
import com.hbtl.view.CircleImageView;
import com.hbtl.view.RainbowBar;
import com.hbtl.view.ToastHelper;
import com.hbtl.view.ToastHelper.ToastType;
import com.otg.idcard.OTGReadCardAPI;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import timber.log.Timber;

// 在 ANDROID 程序中禁止屏幕旋转和避免重启ACTIVITY
// http://www.sunnyu.com/?p=223
public class MainMenuActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final static int AR_SCAN_QR_CODE = 0x1;
    private final static int AR_FACE_DETECTOR_CODE = 0x2;

    // 人脸识别验证需要的授权
    //private final static int RP_CAMERA_CODE = 0x101;
    //private final static int RP_READ_EXTERNAL_STORAGE_CODE = 0x102;

    // 软件动态权限申请...
    private final static int RP_START_INTENT_CODE = 0x000;// 启动通用授权
    private final static int RP_VAE_INTENT_CODE = 0x001;// 人脸识别验证需要的授权
    private final static int RP_CAU_INTENT_CODE = 0x002;// 软件更新需要的授权

    protected static final String TAG = "MainMenuActivity";
    private Activity mActivity;

    private MenuItem myAccountSpace_V;
    private MenuItem myAppSetting_V;
    @BindView(R.id.mDrawerLayout) DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle space_drawerToggle; // DrawerLayout - Toggle

    private CircleImageView aInfoPicture_CIV;
    private TextView aInfoName_TV;
    private TextView deviceId_TV;// 设备编号...

    private MenuItem[] myAccountSlideMenuItemViews;

    // 离线上传错误步递...
    private int upCoveSteps = 0;

    // Run Service
    private DownloadReceiver downloadReceiver = null;
    //private NetWorkStateReceiver nwsReceiver;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;
    @BindView(R.id.commonQrCodeScan_Btn) LinearLayout commonQrCodeScan_Btn;
    @BindView(R.id.commonQrCodeBody_Btn) LinearLayout commonQrCodeBody_Btn;
    @BindView(R.id.myTakeAvatar_IV) ImageView myTakeAvatar_IV;
    @BindView(R.id.appRunVersion_TV) TextView appRunVersion_TV;
    @BindView(R.id.appMainAuthEnter_TV) TextView appMainAuthEnter_TV;
    @BindView(R.id.appNoAuthEnter_TV) TextView appNoAuthEnter_TV;
    @BindView(R.id.appUpAuthTs_TV) TextView appUpAuthTs_TV;
    @BindView(R.id.navigation_View) NavigationView navigation_View;
    @BindView(R.id.commonIcReaderStatus_TV) TextView commonIcReaderStatus_TV;
    @BindView(R.id.commonIcReaderStatus_RBar) RainbowBar commonIcReaderStatus_RBar;
    @BindView(R.id.commonOaeUploadStatus_RBar) RainbowBar commonOaeUploadStatus_RBar;

    // Realm 数据库实例
    Realm mRealmInstance;
    private Subscription mSubscription;

    // 动态权限申请...
    RxPermissions rxPermissions;

    // 认证验证对象
    private EnterAuthModel mEnterAuthModel = null;// 认证二维码...

    private OTGReadCardAPI nfcReadCardAPI = null;
    private Intent onIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu_activity);
        mActivity = MainMenuActivity.this;
        ButterKnife.bind(this);

        // 动态权限申请实例...
        rxPermissions = new RxPermissions(this);

        appNav_ToolBar.setTitle("武汉城市圈旅游e卡通");
        // Inflate a menu to be displayed in the toolbar
        appNav_ToolBar.inflateMenu(R.menu.common_account_space_menu);
        setSupportActionBar(appNav_ToolBar);
        // 注意与 setSupportActionBar 的顺序
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        // 如果是自己的账户空间,则初始化 DrawerMenu 侧滑菜单
        initDrawer();
        initDrawerMenuViews();
        if (BuildConfig.DEBUG) {
            appUpAuthTs_TV.setVisibility(View.VISIBLE);
        }

        commonQrCodeBody_Btn.setOnClickListener(this);
        commonQrCodeScan_Btn.setOnClickListener(this);

        // 注册事件监听器
        EventBus.getDefault().register(this);

        int currentCode = CommonUtils.getVersionCode(mActivity);
        appRunVersion_TV.setText(String.valueOf(currentCode));

        // 展示 Gif 图片...
        Glide.with(mActivity)
                .load(R.drawable.read_ic)
                //.load("https://qs-mo.coam.co/saoma.gif")
                //.placeholder(R.drawable.loading_gif)
                .into(myTakeAvatar_IV);

        ///////////////////////////////////////////////
        // 初始化 RealmInstance
        mRealmInstance = Realm.getDefaultInstance();

        // 初始化认证信息...
        mEnterAuthModel = new EnterAuthModel();
        mEnterAuthModel.authWay = "noAuth";
        mEnterAuthModel.authState = "no";

        // 更新 App 密钥
        CommonUtils.getInstance().appLoadEncryptKey(mActivity);

        // 查询 App 有效入园次数..
        CommonUtils.getInstance().appQueryEnterCount();

        // 入园统计显示...
        renderAuthView();

        // Must be done during an initialization phase like onCreate
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        // Always true pre-M
                        // 检查软件更新...
                        CommonUtils.getInstance().checkAppUpdate(mActivity);
                        Timber.i("`permission.name` is granted !");
                    } else {
                        Timber.w("Denied permission with ask never again");
                    }
                });

        // 刷卡模块...
        nfcReadCardAPI = new OTGReadCardAPI(getApplicationContext());
        // 刷卡日志[/yishu/yishu.txt]开关...
        nfcReadCardAPI.setlogflag(1);
    }

    // 更新离线认证数据统计
    public void renderAuthView() {
        // 更新离线数据显示...
        // 获取本地离线数据...
        int oAuthInfoSize = mRealmInstance.where(EnterAuthModel.class).findAll().size();
        // 更新离线数据...
        appNoAuthEnter_TV.setText(String.valueOf(oAuthInfoSize));
    }

    // [Android系统NFC读写简介](https://www.apkdv.com/introduction-to-the-android-nfc-speaking-reading-and-writing/)
    // [NFC在Android中的应用](http://www.jianshu.com/p/7991dc02b0d8)
    // [Android NFC问道](http://www.jianshu.com/p/cf606ad4f8b3)
    private String getIntentIcCode(Intent intent) {
        //获取 Tag 对象数据,用于解析出 id 和 所适配的 tech
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] tagId = tag.getId();
        //将 byte[] 转换为 十六进制字符
        String idStr = Md5Utils.byteArrayToHexString(tagId);

        String mInfo = "TypeID : " + idStr + "\n";
        // 获取 tag 中能于 android nfc 适配的 tech
        for (int i = 0; i < tag.getTechList().length; i++) {
            mInfo += "Tech-" + (i + 1) + " : " + tag.getTechList()[i] + "\n";
        }
        //Toast.makeText(mActivity, idStr, Toast.LENGTH_SHORT).show();
        Timber.i("[idStr: " + idStr + "]...................................................................");
        Timber.i("[mInfo: " + mInfo + "]...................................................................");

        return idStr;
    }

    // 初始化抽屉
    private void initDrawer() {
        // 如果是当前账号的空间,则绑定左滑 DrawerToggle 菜单
        space_drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, appNav_ToolBar, R.string.nav_drawer_open, R.string.nav_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ///getSupportActionBar().hide();
                //Timber.i("== ActionBarDrawerToggle --> onDrawerOpened...");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ///getSupportActionBar().show();
                //Timber.i("== ActionBarDrawerToggle --> onDrawerClosed...");
            }
        };

        mDrawerLayout.setDrawerListener(space_drawerToggle);//必须有这一步,否则崩溃
        space_drawerToggle.syncState();//暂不知道有什么用,参考 http://stackoverflow.com/questions/31243454/changing-hamburger-action-bar-icon-to-rotating-arrow-not-working-when-opening-t
        ///mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //将侧边栏顶部延伸至status bar
                mDrawerLayout.setFitsSystemWindows(true);
                //将主页面顶部延伸至status bar;虽默认为false,但经测试,DrawerLayout需显示设置
                mDrawerLayout.setClipToPadding(false);
            }
        }

        //https://segmentfault.com/a/1190000004151222
//        navigation_View = (NavigationView) findViewById(R.id.nav_view);
        navigation_View.setNavigationItemSelectedListener(this);

        myAccountSpace_V = navigation_View.getMenu().findItem(R.id.myAccountSpace_V);
        myAppSetting_V = navigation_View.getMenu().findItem(R.id.myAppSetting_V);

//        View headerLayout = navigation_View.inflateHeaderView(R.layout.menu_common_account_space_nav_header_main);
        View headerLayout = navigation_View.getHeaderView(0);
        LinearLayout headerBackground = (LinearLayout) headerLayout.findViewById(R.id.header_background);

        aInfoPicture_CIV = (CircleImageView) headerBackground.findViewById(R.id.aInfoPicture_CIV);
        aInfoName_TV = (TextView) headerBackground.findViewById(R.id.aInfoName_TV);

        // 设定设备编号...
        deviceId_TV = (TextView) headerBackground.findViewById(R.id.deviceId_TV);
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        deviceId_TV.setText(deviceId);
    }

    private void initDrawerMenuViews() {
        myAccountSlideMenuItemViews = new MenuItem[]{myAccountSpace_V, myAppSetting_V};
        //依次构建侧边栏菜单并绑定点击事件
        for (int i = 0; i < myAccountSlideMenuItemViews.length; i++) {
            //分别设定选定状态
            if (i == 0) {
                myAccountSlideMenuItemViews[i].setChecked(true);
            } else {
                myAccountSlideMenuItemViews[i].setChecked(false);
            }
        }
    }

    // 重写屏幕旋转的方法 参考 http://www.sunnyu.com/?p=223
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//            //横向
//            setContentView(R.layout.file_list_landscape);
//        }else{
//            //竖向
//            setContentView(R.layout.file_list);
//        }
    }

    CommonMyHsAreaInfo mMyHsArea = new CommonMyHsAreaInfo();
    private static final int CHOOSE_PLACE_REQUESTCODE = 0x5;
    private ArrayList<CommonMyHsAreaInfo> myHsAreaList;

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.commonQrCodeBody_Btn: // 通用二维码扫码

                // 更新 App 有效入园次数...
                CommonUtils.getInstance().appQueryEnterCount();

                if (BuildConfig.DEBUG) {
//                boolean debug = BuildConfig.DEBUG;
//                String buildType = BuildConfig.BUILD_TYPE;
//                String buildApiUrl = BuildConfig.API_URL;
//                String apiServerUrl = BuildConfig.API_SERVER_URL;
//                boolean logHttpCalls = BuildConfig.LOG_HTTP_CALLS;
//                String flavor = BuildConfig.FLAVOR;
//                Timber.d("[BBBB][=========================================================================]");
//                Timber.d("[BBBB][debug: " + debug + "]");
//                Timber.d("[BBBB][buildType: " + buildType + "]");
//                Timber.d("[BBBB][buildApiUrl: " + buildApiUrl + "]");
//                Timber.d("[BBBB][apiServerUrl: " + apiServerUrl + "]");
//                Timber.d("[BBBB][logHttpCalls: " + logHttpCalls + "]");
//                Timber.d("[BBBB][flavor: " + flavor + "]");
//                ToastHelper.makeText(mActivity, "[#][debug: " + debug + "][buildType: " + buildType + "][buildApiUrl: " + buildApiUrl + "][apiServerUrl: " + apiServerUrl + "][logHttpCalls: " + logHttpCalls + "][flavor: " + flavor + "]...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();
//
//                if (BuildConfig.DEBUG) {
//                    //Timber.plant(new Timber.DebugTree());
//                    ToastHelper.makeText(mActivity, "[#][debug: true]...", ToastHelper.LENGTH_SHORT, ToastType.WARNING).show();
//                } else {
//                    ToastHelper.makeText(mActivity, "[#][debug: false]...", ToastHelper.LENGTH_SHORT, ToastType.WARNING).show();
//                }

                    // ndk test...
                    EktCso ndkTest = new EktCso();
                    String ndkTs = ndkTest.getString();

                    Timber.i("[ndkTs: " + ndkTs + "]");

                    String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
                    Timber.i("[deviceId: " + deviceId + "]");

                    // 离线数据上报...
                    CommonUtils.getInstance().appGateDataReport();
                    if (true) return;

                    // AesUtils 加解密测试...
//                try {
//                    AesUtils.test();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (true) return;

                    // Rsa 公钥 私钥 测试...
                    //RsaUtils.testRsaCa();
                    //Timber.i("deSignature:" + signature);

                    // 验证二维码签名...
                    //String qrCodeInfo = "ekatong@00tlnkw8LcpNAj1Sxn1LNJ|1510912648|176FA00C6BEE6702076E9F21A88E5E89AAC3CAEA";
                    String qrCode = "ekatong@007GlNR17MAbU+PippC6OmW+KVsCRuq+zF3lKj4gtw8lIMIb10fGd6mH9JlSOaGqGphc9hdZmtjqpt4pgwxHX/IxtGLhEN6fcCE0qH4mNw85Y=";
                    EnterAuthModel enterAuthInfo = CommonUtils.getInstance().loadQrCodeInfo(mActivity, qrCode);
                    boolean verifyResult = CommonUtils.getInstance().vsQrCodeInfo(enterAuthInfo);

                    // 更新 App 密钥
                    //CommonUtils.getInstance().appLoadEncryptKey();

                    // 查询 App 有效入园次数
                    //CommonUtils.getInstance().appQueryEnterCount();

                    // 检查软件更新...
                    //checkAppUpdate();
                    //new CommonUtils().checkAppUpdate(mActivity);

                    // 设备注册...
                    //CommonUtils.getInstance().appDeviceRegister(mActivity);

                    intent = new Intent(MainMenuActivity.this, CommonSearchAreaLineListActivity.class);
                    intent.putParcelableArrayListExtra("myHsAreaList", myHsAreaList);
                    intent.putExtra("myHsArea", mMyHsArea);
                    //startActivityForResult(intent, CHOOSE_PLACE_REQUESTCODE);
                }
                break;
            case R.id.commonQrCodeScan_Btn: // 通用二维码扫码
                // 跳至二维码扫码窗口...
                gotoQrScanActivity();
                break;
            default:
                // TODO...
                Timber.i("iwwwwwwwwwwwwwwwwwwwwwwwww---");
                break;
        }
    }

    // 调到授权认证结果页面(二维码离线认证)...
    public void gotoQroScenicAuthActivity(EnterAuthModel enterAuthModel, long authEnterDts, String authFacePath, boolean retainFace, int authCode, String authResult) {

        // 保存到本地数据...
        //EnterAuthModel enterAuthInfo = mRealmInstance.createObject(EnterAuthModel.class);
        EnterAuthModel enterAuthInfo = new EnterAuthModel();
        enterAuthInfo.authWay = mEnterAuthModel.authWay;
        enterAuthInfo.qrCode = mEnterAuthModel.qrCode;
        enterAuthInfo.qrCardSn = mEnterAuthModel.qrCardSn;
        enterAuthInfo.qrCardTs = mEnterAuthModel.qrCardTs;
        enterAuthInfo.captureImgFn = authFacePath;
        enterAuthInfo.authEnterDts = authEnterDts;//String.valueOf(dts);

        // Persist your data easily
        mRealmInstance.beginTransaction();
        EnterAuthModel nEnterAuthInfo = mRealmInstance.copyToRealmOrUpdate(enterAuthInfo);
        mRealmInstance.commitTransaction();

        // 获取本地缓存索引编号...
        String qrId = enterAuthInfo.qrId;

        CoamResponseInner.MemberInfo memberInfo = null;

        Intent intent = new Intent(MainMenuActivity.this, ScenicAuthActivity.class);
        intent.putExtra("authWay", enterAuthModel.authWay);
        intent.putExtra("authFacePath", authFacePath);
        intent.putExtra("retainFace", retainFace);
        intent.putExtra("authCode", authCode);
        intent.putExtra("authResult", authResult);
        intent.putExtra("memberInfo", memberInfo);
        startActivity(intent);

        // 更新刷卡验证状态[防止(Activity)跳转延迟]...
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mEnterAuthModel.authState = "complete";
            }
        }, 1000);
    }

    // 调到授权认证结果页面...
    public void gotoScenicAuthActivity(CoamResponseInner.MemberInfo memberInfo, String authFacePath, boolean retainFace, int authCode, String authResult) {

        Intent intent = new Intent(MainMenuActivity.this, ScenicAuthActivity.class);
        intent.putExtra("authWay", mEnterAuthModel.authWay);
        intent.putExtra("authFacePath", authFacePath);
        intent.putExtra("retainFace", retainFace);
        intent.putExtra("authCode", authCode);
        intent.putExtra("authResult", authResult);
        intent.putExtra("memberInfo", memberInfo);
        startActivity(intent);

        // 更新刷卡验证状态[防止(Activity)跳转延迟]...
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mEnterAuthModel.authState = "complete";
            }
        }, 1000);
    }

    public void gotoQrScanActivity() {
        // 判断当前是否正处于读卡认证状态...

        // 获取当前已设定认证方式及状态
        String authWay = mEnterAuthModel.authWay;
        String authState = mEnterAuthModel.authState;

        // [start:开始验证|waiting:等待验证|interrupt:验证中断|complete:验证结束]
        switch (authState) {
            case "no":
                // TODO: (首次未验证)继续下一步认证
                break;
            case "start":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "waiting":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "detector":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "verify":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "interrupt":
                // TODO: (验证信息已中断)继续下一步认证...
                break;
            case "complete":
                // TODO: (验证信息已完成)继续下一步认证
                break;
            default:
                // TODO: ...
                ToastHelper.makeText(mActivity, "[#][抱歉,未知认证方式(authWay: " + authWay + ")及状态(authState: " + authState + ")]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
                return;
        }

        // 递增统计计数[限定无操作 1 分钟后启用上传计划任务]...
        int wTs = 6;
        int mwTs = 6;
        if (CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs < wTs) CommonUtils.addLoopWaitUpTs(wTs, mwTs);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Timber.e("EEError:|attach_gallery...");

            if (mActivity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Android-6.0 未授权[CAMERA]访问问题
                ToastHelper.makeText(mActivity, "[#]未授权(CAMERA)...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                requestPermissions(new String[]{Manifest.permission.CAMERA}, RP_VAE_INTENT_CODE);
                return;
            }

            if (mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Android-6.0 未授权[READ_EXTERNAL_STORAGE]访问问题
                ToastHelper.makeText(mActivity, "[#]未授权(READ_EXTERNAL_STORAGE)...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RP_VAE_INTENT_CODE);
                return;
            }

            // 权限检查通过提示
            ToastHelper.makeText(mActivity, "[@]已完成授权...", ToastHelper.LENGTH_LONG, ToastType.SUCCESS).show();
        } else {
            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_低版本无需授权...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();
        }

        // 认证方式...
        mEnterAuthModel.authWay = "QrCode";
        mEnterAuthModel.authState = "start";

        // 调用扫码校验程序...
        Intent intent = new Intent();
        intent.setClass(mActivity, CommonScanQrCodeActivity.class);//扫码
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, AR_SCAN_QR_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//                for (int a = 0; a < permissions.length; a++) {
//                    // if (grantResults.length <= a || grantResults[a] != PackageManager.PERMISSION_GRANTED) {
//                    //     continue;
//                    //}
//                    switch (permissions[a]) {
//                        case Manifest.permission.CAMERA:
//                            if (grantResults[a] == PackageManager.PERMISSION_DENIED) {
//                                ToastHelper.makeText(mActivity, "请授权应用打开摄像头权限...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.WARNING).show();
//                            } else if (grantResults[a] == PackageManager.PERMISSION_GRANTED) {
//                                //　重新发起扫描
//                                commonQrCodeScan_Btn.performClick();
//                            }
//                            break;
//                    }
//                }

        switch (requestCode) {
            case RP_VAE_INTENT_CODE:// 扫码-摄像头|存储
                commonQrCodeScan_Btn.performClick();
                break;
            case RP_CAU_INTENT_CODE:// 软件更新-存储
                break;
            default:
                // TODO: ...
                ToastHelper.makeText(mActivity, "未知授权码[requestCode: " + requestCode + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                break;
        }
        Timber.i("UUU|6.");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        // 下载更新服务...
        downloadReceiver = new DownloadReceiver();

        // App 更新下载进度...
        IntentFilter filter = new IntentFilter();
        filter.addAction(CoamBuildVars.ACTION_DOWNLOAD_PROGRESS);
        filter.addAction(CoamBuildVars.ACTION_DOWNLOAD_SUCCESS);
        filter.addAction(CoamBuildVars.ACTION_DOWNLOAD_FAIL);
        registerReceiver(downloadReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // App 更新下载器...
        if (downloadReceiver != null) unregisterReceiver(downloadReceiver);

        // 手动关闭后台下载服务...
        CommonUtils.getInstance().stopDownloadService(mActivity);

        //取消注册事件监听器
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);

        // 删除所有监听事件...
        if (mRealmInstance != null) mRealmInstance.removeAllChangeListeners();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Timber.d(TAG + "onKeyDown keyCode = " + keyCode + " repeatCount = " + event.getRepeatCount());
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(true);
                if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_监听到回退键...", ToastHelper.LENGTH_SHORT, ToastType.WARNING).show();
                return true;
            case KeyEvent.KEYCODE_BUTTON_A:
                // 判断按钮状态,防止重复触发点击事件...
                if (commonQrCodeScan_Btn.isEnabled()) {
                    commonQrCodeScan_Btn.setEnabled(false);
                    if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_监听到扫描键(onKeyDown)...", ToastHelper.LENGTH_SHORT, ToastType.WARNING).show();
                    // 跳至二维码扫码窗口...
                    gotoQrScanActivity();
                }
                break;
            default:
                // TODO...
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Timber.d(TAG + "onKeyUp keyCode=" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                commonQrCodeScan_Btn.setEnabled(true);
                if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_监听到扫描键[onKeyUp]...", ToastHelper.LENGTH_SHORT, ToastType.WARNING).show();
                return true;
            default:
                // TODO...
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // 如果直接按回退按钮,无返回结果...
        if (resultCode != RESULT_OK) {
            Timber.d(TAG + "canceled or other exception!");
            // 更新验证状态...
            mEnterAuthModel.authState = "interrupt";
            return;
        }

        switch (requestCode) {
            case AR_SCAN_QR_CODE:
                Bundle bundle = data.getExtras();
                //显示扫描到的内容
                String qrResult = bundle.getString("result");
                Timber.i("#####[qrResult: " + qrResult + "]");
                // QrCodeManager qrCodeManager = new QrCodeManager(mActivity, qrResult);
                // qrCodeManager.dealQrInfo();

                // 验证二维码签名...
                String qrCode = qrResult;
                //String qrCode = "ekatong@007GlNR17MAbU+PippC6OmW9kfWC5U9X7/pCIgfmnixixG0lbYX8kSxsovxTeFzp5QDI4rPIiGFZ1EpnkX6VkOIv4xpqw16fSOdsTLzC6roNs=";
                EnterAuthModel enterAuthInfo = CommonUtils.getInstance().loadQrCodeInfo(mActivity, qrCode);

                // 保存认证授权信息
                mEnterAuthModel.authWay = "QrCode";
                mEnterAuthModel.qrCode = qrResult;

                boolean qrVerifyResult = false;// 二维码认证结果...
                // 如果二维码解密失败,则返回 null...
                if (enterAuthInfo == null) {
                    // 判断是否是为离线明文二维码串...
                    if (qrCode.length() >= 10 && qrCode.substring(0, 8).equals("ekatong@") && (qrCode.length() == 24 || qrCode.length() == 26)) {
                        qrVerifyResult = true;
                        // 设置为明文二维码验证...
                        mEnterAuthModel.authWay = "QrCove";

                        // 判断是否有网...
                        if (!NetworkStateService.isNetworkAvailable(mActivity)) {
                            // 无网络状态下仅支持离线二维码认证...
                            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "当前网络不佳,暂不支持明文二维码离线验证,入园验证失败...", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();

                            // 明文二维码不支持离线验证,终端抓取人脸步骤...
                            // 更新验证状态...
                            mEnterAuthModel.authState = "interrupt";
                            return;
                        }
                    }
                } else {
                    qrVerifyResult = CommonUtils.getInstance().vsQrCodeInfo(enterAuthInfo);
                    // 保存解码数据...
                    if (qrVerifyResult) {
                        mEnterAuthModel.qrCardSn = enterAuthInfo.qrCardSn;
                        mEnterAuthModel.qrCardTs = enterAuthInfo.qrCardTs;
                        mEnterAuthModel.qrCardSign = enterAuthInfo.qrCardSign;
                    }
                }

                // 如果二维码校验通过...
                if (qrVerifyResult) {
                    // 更新验证状态...
                    mEnterAuthModel.authState = "detector";

                    // 调用扫码校验程序...
                    Intent intent = new Intent();
                    intent.setClass(mActivity, FaceDetectorActivity.class);//人脸识别
                    startActivityForResult(intent, AR_FACE_DETECTOR_CODE);
                    return;
                }

                // 更新验证状态...
                mEnterAuthModel.authState = "interrupt";

                // 二维码有问题(无效二维码)...
                ToastHelper.makeText(mActivity, "[@][二维码验证失败]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
                break;
            case AR_FACE_DETECTOR_CODE:
                // 通过 EventBus 事件通知返回(unreachable)...
                Timber.d(TAG + "@AR_FACE_DETECTOR_CODE");
                // 人脸抓取完成回调
                ToastHelper.makeText(mActivity, "[@][人脸图像抓取完成]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
                break;
            default:
                // TODO...
                break;
        }
    }

    // 认证唯一编码...
    public String icVerifyWay = "";// [osp:服务商|msp:服务网]...
    public String icVerifyNo = String.valueOf(0);// 验证编号...
    public long nicVerifyTs = 0;

    // 使用异步线程处理,避免阻塞 UI 进程...
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventThread(CrossIcReaderInfoBus crossIcReaderInfoBus) {
        //可能有异常
        if (crossIcReaderInfoBus == null) return;

        // 获取刷卡通知事件...
        String readWay = crossIcReaderInfoBus.readWay;
        int readCode = crossIcReaderInfoBus.readCode;
        String verifyWay = crossIcReaderInfoBus.verifyWay;
        long verifyTs = crossIcReaderInfoBus.verifyTs;
        String msIcNo = crossIcReaderInfoBus.msIcNo;

        // 仅支持刷卡初步消息推送,防止冲突(ThreadMode.ASYNC -> ThreadMode.MAIN)...
        //if (readCode != 0) return;
        if (!mEnterAuthModel.authState.equals("start")) return;

        // 认证唯一编码-过期结果作废...
        icVerifyWay = "";
        icVerifyNo = String.valueOf(0);
        nicVerifyTs = System.currentTimeMillis();

        Timber.e("[@@@][onEventThread(CrossIcReaderInfoBus)][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "][nicVerifyTs: " + nicVerifyTs + "](0)");

        // 获取身份证卡码...
        icVerifyNo = getIntentIcCode(onIntent);

        // 更新刷卡验证状态(防止二次校验)...
        mEnterAuthModel.authState = "waiting";

        //1.[@] 检索身份证在线缓存...
        new CommonUtils().appCheckIdCode(mActivity, icVerifyNo, nicVerifyTs);

        //2.[@] 检索身份证调用厂商接口查询...
        Timber.d("[NFC_LOG_TAG]come into MESSAGE_CLEAR_ITEMS 1");
        // 身份证读卡模块
        String loginTicket = "987654321123456789";
        int nfcIcCode = nfcReadCardAPI.NfcReadCard(onIntent, loginTicket);
        Timber.d("[NFC_LOG_TAG]come into MESSAGE_CLEAR_ITEMS 2");
        Timber.e("For Test ReadCard nfcIcCode=" + nfcIcCode);

        // 重建刷卡时间通知...
        crossIcReaderInfoBus.readWay = "NFC";
        crossIcReaderInfoBus.readCode = nfcIcCode;
        crossIcReaderInfoBus.verifyWay = "osp";
        crossIcReaderInfoBus.verifyTs = nicVerifyTs;

        EventBus.getDefault().post(crossIcReaderInfoBus);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CrossIcReaderInfoBus crossIcReaderInfoBus) {
        // 可能有异常
        if (crossIcReaderInfoBus == null) return;

        // 获取刷卡异步通知结果事件...
        String readWay = crossIcReaderInfoBus.readWay;
        int readCode = crossIcReaderInfoBus.readCode;
        String verifyWay = crossIcReaderInfoBus.verifyWay;
        long verifyTs = crossIcReaderInfoBus.verifyTs;
        String msIcNo = crossIcReaderInfoBus.msIcNo;

        // 不支持刷卡识别消息推送,防止冲突(ThreadMode.ASYNC -> ThreadMode.MAIN)...
        //if (readCode == 0) return;

        // 延迟的返回通知结果会被丢弃(可能前一步已拿到正确的认证结果)...
        if (!mEnterAuthModel.authState.equals("waiting")) {
            // 延迟通知结果,作废处理...
            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_延迟通知结果,作废处理[authState: " + mEnterAuthModel.authState + "]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
            return;
        }

        // 延迟的返回通知结果会被丢弃...
        if (nicVerifyTs != 0 && nicVerifyTs != verifyTs) {
            // 延迟通知结果,作废处理...
            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_延迟通知结果,作废处理[verifyTs: " + verifyTs + "]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
            return;
        }

        Timber.e("[@@@][onEventMainThread(CrossIcReaderInfoBus)][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "][readCode: " + readCode + "][nicVerifyTs: " + nicVerifyTs + "](000)");

        //final Map<String, String> ctParamList = new HashMap<String, String>();
        //ctParamList.put("2", "接收数据超时!");
        //ctParamList.put("41", "读卡失败!");
        //ctParamList.put("42", "没有找到服务器!");
        //ctParamList.put("43", "服务器忙!");
        //ctParamList.put("90", "读卡成功!");
        //ctParamList.put("227", "服务器检索通过!");
        //ctParamList.put("-227", "无卡码数据缓存!");
        //ctParamList.put("-228", "缓存服务器异常!");

        // 验证服务方...
        switch (verifyWay) {
            case "osp"://服务商
                switch (readCode) {
                    case 2:
                    case 41:
                    case 42:
                    case 43:
                        // 如果尚未完成验证(osp),更新刷卡验证状态...
                        switch (icVerifyWay) {
                            case "osp":
                                // TODO: unreachable...
                                break;
                            case "msp":
                                // 已完成 osp 验证失败后...
                                mEnterAuthModel.authState = "interrupt";
                                break;
                            default:
                                // TODO: null...
                                mEnterAuthModel.authState = "waiting";
                                break;
                        }
                        break;
                    case 90:
                        String icName = nfcReadCardAPI.Name();
                        String icSex = nfcReadCardAPI.SexL();
                        String icNation = nfcReadCardAPI.NationL();
                        String icBorn = nfcReadCardAPI.BornL();
                        String icAddress = nfcReadCardAPI.Address();
                        String icNo = nfcReadCardAPI.CardNo();
                        String icPolice = nfcReadCardAPI.Police();
                        String icActivityTime = nfcReadCardAPI.Activity();
                        String icActivityTime2 = nfcReadCardAPI.ActivityL();
                        String icDnCode = nfcReadCardAPI.DNcode();

                        byte[] idAvatarBts = nfcReadCardAPI.GetImage();
                        Bitmap idAvatarBitmap = ImageUtils.Bytes2Bimap(idAvatarBts);
                        //Timber.w("[name: " + nfcReadCardAPI.Name() + "][CardNo: " + nfcReadCardAPI.CardNo() + "]...");

                        nfcReadCardAPI.release();

                        Timber.i("身份证读取成功[name: " + icName + "][CardNo: " + icNo + "]...");

                        // 设定验证方式...
                        mEnterAuthModel.authWay = "IdCard";
                        mEnterAuthModel.icNo = icNo;

                        // 如果刷卡验证通过,则调到下一步(人脸抓取)...
                        mEnterAuthModel.authState = "detector";
                        break;
                    default:
                        // TODO: ...
                        ToastHelper.makeText(mActivity, "Sorry: unknown [verifyWay: " + verifyWay + "][readCode: " + readCode + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                }
                break;
            case "msp"://服务网
                switch (readCode) {
                    case CoamHttpService.IdCardNoCode:
                    case CoamHttpService.IdCardErrorCode:
                        // 如果尚未完成验证(osp),更新刷卡验证状态...
                        switch (icVerifyWay) {
                            case "osp":
                                // 已完成 osp 验证失败后...
                                mEnterAuthModel.authState = "interrupt";
                                break;
                            case "msp":
                                // TODO: unreachable...
                                break;
                            default:
                                // TODO: null...
                                mEnterAuthModel.authState = "waiting";
                                break;
                        }
                        break;
                    case CoamHttpService.IdCardHasCode:
                        // 设定验证方式...
                        mEnterAuthModel.authWay = "IdCard";
                        mEnterAuthModel.icNo = msIcNo;

                        // 如果刷卡验证通过,则调到下一步(人脸抓取)...
                        mEnterAuthModel.authState = "detector";
                        break;
                    default:
                        // TODO: ...
                        ToastHelper.makeText(mActivity, "Sorry: unknown [verifyWay: " + verifyWay + "][readCode: " + readCode + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                }
                break;
            default:
                // TODO: ...
                ToastHelper.makeText(mActivity, "Sorry: unknown [verifyWay: " + verifyWay + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                break;
        }

        // 获取编码信息...
        //String text = ctParamList.get(String.valueOf(readCode));
        //commonIcReaderStatus_TV.setText(text);

        // 判断认证结果...
        switch (mEnterAuthModel.authState) {
            case "waiting":
                // 设定为已验视服务方...
                icVerifyWay = verifyWay;

                // 读卡完成!
                commonIcReaderStatus_TV.setText("继续读卡...");

                if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_[继续等待卡码检索结果](icVerifyWay: " + icVerifyWay + ")", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
                break;
            case "detector":
                // 设定为已验视服务方...
                icVerifyWay = "";

                // 读卡完成!
                commonIcReaderStatus_TV.setText("读卡成功!");

                // 调用扫码校验程序...
                Intent intent = new Intent();
                intent.setClass(mActivity, FaceDetectorActivity.class);//人脸识别
                startActivityForResult(intent, AR_FACE_DETECTOR_CODE);

                //commonIcReaderStatus_TV.setText("请将身份证放置于机器上方...");
                commonIcReaderStatus_RBar.setVisibility(View.GONE);

                // 解决 ToastHelper 弹出 Activity 跳转问题[android.view.WindowLeaked: Activity com.hbtl.ui.common.activity.CommonWelcomeSplashActivity has leaked window android.widget.LinearLayout]
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        commonIcReaderStatus_TV.setText("请将身份证放置于机器上方...");
                    }
                }, 1000);
                break;
            case "interrupt":
                // 设定为已验视服务方...
                icVerifyWay = "";

                // 读卡失败...
                commonIcReaderStatus_TV.setText("抱歉,读卡失败!");

                if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_[卡码[服务方]检索失败]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();

                //commonIcReaderStatus_TV.setText("请将身份证放置于机器上方...");
                commonIcReaderStatus_RBar.setVisibility(View.GONE);

                // 解决 ToastHelper 弹出 Activity 跳转问题[android.view.WindowLeaked: Activity com.hbtl.ui.common.activity.CommonWelcomeSplashActivity has leaked window android.widget.LinearLayout]
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        commonIcReaderStatus_TV.setText("请将身份证放置于机器上方...");
                    }
                }, 1000);
                break;
            default:
                // TODO: ...
                ToastHelper.makeText(mActivity, "Sorry: unknown [authState: " + mEnterAuthModel.authState + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                break;
        }
    }

    private MaterialDialog authWaitingDialog;

    // 登陆Http接口调用
    @Subscribe
    public void onEventMainThread(CrossCommonAppFaceDetectorBus crossCommonAppFaceDetectorBus) {
        //可能有异常
        if (crossCommonAppFaceDetectorBus == null) return;

        if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_人脸识别的-事件总线消息...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();

        authWaitingDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();
        authWaitingDialog.setCancelable(false);

        // 更新刷卡验证状态...
        mEnterAuthModel.authState = "verify";

        Timber.e("[@@@][onEventMainThread()][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "][qrCardTs: " + mEnterAuthModel.qrCardTs + "](1)");

        String captureWay = crossCommonAppFaceDetectorBus.captureWay;
        String captureFacePath = crossCommonAppFaceDetectorBus.captureFacePath;
        switch (captureWay) {
            case "faceDetector":
                // 保存入园时间戳...
                long ts = System.currentTimeMillis();
                long dts = ts / 1000 / (60 * 60 * 24);

                // 分入园方式...
                switch (mEnterAuthModel.authWay) {
                    case "IdCard":
                        // TODO: ...
                        Timber.i("[authWay: " + mEnterAuthModel.authWay + "]");
                        break;
                    case "QrCove":
                        // TODO: ...
                        break;
                    case "QrCode":
                        Timber.i("[authWay: " + mEnterAuthModel.authWay + "]");
                        // 首先查询本地离线数据是否已有入园次数限制...
                        // 获取本地离线数据...
                        EnterAuthModel oAuthInfoList = mRealmInstance.where(EnterAuthModel.class).equalTo("qrCardSn", mEnterAuthModel.qrCardSn).equalTo("authEnterDts", dts).findFirst();

                        // 如果离线数据已有入园记录
                        if (oAuthInfoList != null) {
                            // 关闭对话框...
                            authWaitingDialog.dismiss();

                            // 传空会员信息...
                            CoamResponseInner.MemberInfo memberInfo = null;

                            // 跳至授权认证结果页面...
                            Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](1)");
                            gotoScenicAuthActivity(memberInfo, captureFacePath, false, CoamHttpService._Code_OutOfEnterCountOneTourToday, "[当天入园次数受限]");
                            return;
                        }
                        break;
                    default:
                        // TODO: ...
                        ToastHelper.makeText(mActivity, "Sorry: unknown [authWay: " + mEnterAuthModel.authWay + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                }

                // 判断是否有网...
                if (!NetworkStateService.isNetworkAvailable(mActivity)) {

                    // 关闭对话框...
                    authWaitingDialog.dismiss();

                    // 传空会员信息...
                    CoamResponseInner.MemberInfo memberInfo = null;
                    switch (mEnterAuthModel.authWay) {
                        case "IdCard":
                            // 仅支持离线二维码认证...
                            ToastHelper.makeText(mActivity, "当前网络不稳定,入园验证失败,请使用二维码验证", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();

                            // 跳至授权认证结果页面...
                            Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](2)");
                            gotoScenicAuthActivity(memberInfo, captureFacePath, false, CoamHttpService._Code_NoNetWork_EnterByIdCard, "验证失败请改为二维码验证");
                            break;
                        case "QrCove":
                            // 仅支持离线二维码认证...
                            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "当前网络不佳,暂不支持明文二维码离线验证,入园验证失败...", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();

                            // 跳至授权认证结果页面...
                            Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](3)");
                            gotoScenicAuthActivity(memberInfo, captureFacePath, false, CoamHttpService._Code_QrCodeError, "明文二维码在线验证失败...");
                            break;
                        case "QrCode":
                            // 跳至授权认证结果页面...
                            gotoQroScenicAuthActivity(mEnterAuthModel, dts, captureFacePath, true, CoamHttpService._Code_OK_EnterByFace, "[人脸入园(离线)认证成功]");

                            // 更新离线数据...
                            renderAuthView();
                            break;
                        default:
                            // TODO: ...
                            ToastHelper.makeText(mActivity, "Sorry: unknown [authWay: " + mEnterAuthModel.authWay + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                            break;
                    }

                    return;
                }

                // Bitmap...
                Bitmap bitmap = ImageUtils.getSmallBitmap(captureFacePath);
                //myTakeAvatar_IV.setImageBitmap(bitmap);
                String face = ImageUtils.bitmapToBase64(bitmap);
                bitmap.recycle();

                String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
                long timestamp = System.currentTimeMillis();
                int versionCode = CommonUtils.getVersionCode(mActivity);
                String seqStr = deviceId + System.currentTimeMillis();
                String seqid = Md5Utils.getMD5(seqStr);

                final Map<String, String> sParamList = new HashMap<String, String>();
                sParamList.put("versionno", String.valueOf(versionCode));
                sParamList.put("deviceid", deviceId);
                // 离线验证时间与二维码生成时间保持一致...
                sParamList.put("timestamp", String.valueOf(mEnterAuthModel.qrCardTs));
                sParamList.put("face", face);// Base64Util 人脸识别编码
                sParamList.put("idcardinputtype", "0");
                sParamList.put("facetype", "1");
                sParamList.put("seqid", seqid);

                switch (mEnterAuthModel.authWay) {
                    case "IdCard":
                        // TODO: ...
                        sParamList.put("idcard", mEnterAuthModel.icNo);// option
                        sParamList.put("qrcode", "0");// option
                        sParamList.put("icCode", icVerifyNo);// option
                        break;
                    case "QrCove":
                        sParamList.put("idcard", "0");// option
                        sParamList.put("qrcode", mEnterAuthModel.qrCode);// option
                        sParamList.put("icCode", "0");// option
                        break;
                    case "QrCode":
                        sParamList.put("idcard", "0");// option
                        sParamList.put("qrcode", mEnterAuthModel.qrCode);// option
                        sParamList.put("icCode", "0");// option
                        break;
                    default:
                        // TODO: ...
                        ToastHelper.makeText(mActivity, "Sorry: unknown [authWay: " + mEnterAuthModel.authWay + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                }

                CoamHttpService.getInstance().appFaceCompare(sParamList)
                        .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppFaceCompareModel>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onComplete() {
                                // 授权认证结果...
                                authWaitingDialog.dismiss();

                                // 递增统计计数[限定无操作 1 分钟后启用上传计划任务]...
                                //int wTs = 6;
                                //int mwTs = 6;
                                //if (CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs < wTs) CommonUtils.addLoopWaitUpTs(wTs, mwTs);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                                e.printStackTrace();
                                //ToastHelper.makeText(mActivity, "抱歉,数据上传验证失败,请重试...", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();

                                authWaitingDialog.dismiss();

                                // 自定义响应认证状态码...
                                String authResult = "当前网络不稳定,完成离线验证";
                                CoamResponseInner.MemberInfo memberInfo = null;

                                // 网络连接失败,保存二维码验证离线缓存数据...
                                switch (mEnterAuthModel.authWay) {
                                    case "IdCard":
                                        // 跳至授权认证结果页面...
                                        Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](4)");
                                        gotoScenicAuthActivity(memberInfo, captureFacePath, false, CoamHttpService._Code_NoNetWork_EnterByIdCard, "验证超时,请重新验证");
                                        break;
                                    case "QrCove":
                                        // 跳至授权认证结果页面...
                                        Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](5)");
                                        gotoScenicAuthActivity(memberInfo, captureFacePath, false, CoamHttpService._Code_QrCodeError, "当前网络不稳定,明文二维码在线验证失败...");
                                        break;
                                    case "QrCode":

//                                    // 删除最后一项...
//                                    //EnterAuthModel nEnterAuthInfo = mRealmInstance.where(EnterAuthModel.class).findAllSorted("qrId").last();
//                                    EnterAuthModel nEnterAuthInfo = mRealmInstance.where(EnterAuthModel.class).findAllSorted("qrId").last();
//
//                                    //If you have an incrementing id column, do this
//                                    //long lastInsertedId = allTransactions.last();
//
//                                    // 人脸识别完成,清除离线数据...
//                                    mRealmInstance.beginTransaction();
//                                    nEnterAuthInfo.deleteFromRealm();
//                                    mRealmInstance.commitTransaction();

                                        // 跳至授权认证结果页面...
                                        gotoQroScenicAuthActivity(mEnterAuthModel, dts, captureFacePath, false, CoamHttpService._Code_OK_WxApp_EnterByIdCard, authResult);

                                        // 更新离线数据显示布局...
                                        renderAuthView();
                                        break;
                                    default:
                                        // TODO: ...
                                        ToastHelper.makeText(mActivity, "Sorry: unknown [authWay: " + mEnterAuthModel.authWay + "]", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                                        break;
                                }
                            }

                            @Override
                            public void onNext(CoamBaseResponse<CoamResponseModel.CsAppFaceCompareModel> model) {
                                // 完成人脸验证请求
                                ToastHelper.makeText(mActivity, "完成人脸识别接口验证请求...", ToastHelper.LENGTH_LONG, ToastType.INFO).show();

                                // 每次扫码后-时时更新有效入园次数...
                                CommonUtils.getInstance().appQueryEnterCount();

                                // 返回的认证状态码...
                                int authCode = model.re_code;
                                String authResult = model.re_desc;

                                // 根据返回结果跳转
                                CoamResponseInner.MemberInfo memberInfo = model.re_data.memberInfo;

                                // 跳至授权认证结果页面...
                                Timber.e("[@@@][authWay: " + mEnterAuthModel.authWay + "][authState: " + mEnterAuthModel.authState + "](6)");
                                gotoScenicAuthActivity(memberInfo, captureFacePath, false, authCode, authResult);

                                Timber.e("#|@|IIIIIIIII[E-M]:");
                            }
                        });
                break;
            default:
                // TODO: ...
                break;
        }
    }

    // 登陆Http接口调用
    @Subscribe
    public void onEventMainThread(CrossUpdateEnterBus crossUpdateEnterBus) {
        if (crossUpdateEnterBus == null) return;

        String updateWay = crossUpdateEnterBus.updateWay;
        int enterCount = crossUpdateEnterBus.enterCount;
        appMainAuthEnter_TV.setText(String.valueOf(enterCount));
    }

    // 登陆Http接口调用
    // [android 使用EventBus的异常](http://www.cnblogs.com/zhaoqingyue/p/6564386.html)
    // 错误[java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()]...
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CrossUploaderAuthInfoBus crossUploaderAuthInfoBus) {
        if (crossUploaderAuthInfoBus == null) return;

        String uploaderWay = crossUploaderAuthInfoBus.uploaderWay;
        String uploaderState = crossUploaderAuthInfoBus.uploaderState;

        // 根据事件通知状态...
        switch (uploaderState) {
            case "notifyUploader":
                // 接收[离线数据上报]服务事件通知入口...
                CommonUtils.getInstance().appGateDataReport();
                break;
            case "waitingUploader":// 等待离线上传计数...
                // 接收[离线数据上报]服务事件通知入口...
                int allowUpTs = CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs;
                appUpAuthTs_TV.setText("[upTs: " + allowUpTs + "]");
                break;
            case "startUploader":
                // 数据上报服务[加锁]...
                CoamApplicationLoader.getInstance().appDeviceInfo.lockAppAuthUploader = true;

                // 显示上传进度条...
                commonOaeUploadStatus_RBar.setVisibility(View.VISIBLE);
                break;
            case "uploaderSuccess":
                // 数据上报服务[解锁]...
                CoamApplicationLoader.getInstance().appDeviceInfo.lockAppAuthUploader = false;

                // 接收[离线数据上报]服务事件通知入口...
                CommonUtils.getInstance().appGateDataReport();

                // 更新离线数据显示...
                // 获取本地离线数据...
//                RealmResults<EnterAuthModel> oAuthInfoList = mRealmInstance.where(EnterAuthModel.class).findAll();
//                Timber.i("[离线数据更新事件]...");
//                for (EnterAuthModel enterAuthInfo : oAuthInfoList) {
//                    Timber.i("[EnterAuthModel]###[@Loop Read@][qrId: " + enterAuthInfo.qrId + "][authWay: " + enterAuthInfo.authWay + "][qrCardSn: " + enterAuthInfo.qrCardSn + "][qrCardTs: " + enterAuthInfo.qrCardTs + "][authEnterDts: " + enterAuthInfo.authEnterDts + "][qrCode: " + enterAuthInfo.qrCode + "]");
//                }
//                // 更新离线数据...
//                appNoAuthEnter_TV.setText(oAuthInfoList.size() ));

                // 入园统计显示...
                renderAuthView();

                // 查询 App 有效入园次数..
                CommonUtils.getInstance().appQueryEnterCount();

                // 关闭上传进度条...
                commonOaeUploadStatus_RBar.setVisibility(View.GONE);
                break;
            case "uploaderError":
                // 设置延迟上传时间...
                int wTs = 1;// 10s 后
                int mwTs = 3;// 30s 后

                // 如果服务器压力过大,或带宽不足会报错误:[java.net.SocketTimeoutException: timeout]...
                upCoveSteps++;
                if (upCoveSteps > 3) {
                    // 上传重试错误超过三次,才延迟至 10 分钟后...
                    upCoveSteps = 0;

                    // 延迟上传,错过高峰期,避免死循环上传数据,耗尽流量(以10分钟为单位递增,最大等待上传时间不超过 10 分钟)...
                    wTs = 6 * 10;
                    mwTs = 6 * 10;
                }

                if (CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs < wTs) CommonUtils.addLoopWaitUpTs(wTs, mwTs);

                // 数据上报服务[解锁]...
                CoamApplicationLoader.getInstance().appDeviceInfo.lockAppAuthUploader = false;

                // 关闭上传进度条...
                commonOaeUploadStatus_RBar.setVisibility(View.GONE);
                break;
            case "uploaderFailed":
                // 数据上报服务[解锁]...
                CoamApplicationLoader.getInstance().appDeviceInfo.lockAppAuthUploader = false;

                // 关闭上传进度条...
                commonOaeUploadStatus_RBar.setVisibility(View.GONE);
                break;
            default:
                // TODO...
                break;
        }

        Timber.i("[###][接收离线数据上报通知][onEventMainThread()][uploaderWay: " + uploaderWay + "][uploaderState: " + uploaderState + "]...");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Timber.i("onNavigationItemSelected" + item.getItemId());
        Intent intent;
        switch (item.getItemId()) {
            case R.id.myAppSetting_V:
                startActivity(new Intent(MainMenuActivity.this, AppSettingActivity.class));
                break;
//            case R.id.myAccountLogOut_V:
//                page = "登出";
//                CoamApplicationLoader.getInstance().logout();
//                // 重新显示登陆页面
//                startActivity(new Intent(mContext, LoginWithRegisterActivity.class));
//                break;
            case R.id.myAppQuit_V:
                //finish();
                //退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void inNewIntent(@NotNull Intent intent) {

//		mHandler.sendEmptyMessageDelayed(MESSAGE_CLEAR_ITEMS, 0);

        //@ 仅支持刷卡入园...

        // 防止在扫码入园的情况下刷卡...
        // if (mEnterAuthModel.authWay != null) return;

        // 获取当前已设定认证方式及状态
        String authWay = mEnterAuthModel.authWay;
        String authState = mEnterAuthModel.authState;

        // [start:开始验证|waiting:等待验证|interrupt:验证中断|complete:验证结束]
        switch (authState) {
            case "no":
                // TODO: (首次未验证)继续下一步认证
                break;
            case "start":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "waiting":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "detector":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "verify":
                Timber.w("[验证操作被拒绝,当前认证(authWay: " + authWay + ")状态为(authState: " + authState + ")...]");
                return;
            case "interrupt":
                // TODO: (验证信息已中断)继续下一步认证...
                break;
            case "complete":
                // TODO: (验证信息已完成)继续下一步认证
                break;
            default:
                // TODO: ...
                ToastHelper.makeText(mActivity, "[#][抱歉,未知认证方式(authWay: " + authWay + ")及状态(authState: " + authState + ")]...", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
                return;
        }

        // 判断是否有网络连接(无网络连接直接拒绝离线验证)...
        if (!NetworkStateService.isNetworkAvailable(mActivity)) {
            // 无网络状态忽略刷卡验证事件通知...
            ToastHelper.makeText(mActivity, "抱歉,无网络,请检查本机网络连接", ToastHelper.LENGTH_LONG, ToastType.WARNING).show();
            return;
        }

        // 递增统计计数[限定无操作 1 分钟后启用上传计划任务]...
        int wTs = 6;
        int mwTs = 6;
        if (CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs < wTs) CommonUtils.addLoopWaitUpTs(wTs, mwTs);

        // 保存 intent ...
        onIntent = intent;

        // 设定验证方式...
        mEnterAuthModel.authWay = "IdCard";
        mEnterAuthModel.authState = "start";

        // 更新显示布局...
        commonIcReaderStatus_TV.setText("正在读卡,请稍等...");
        commonIcReaderStatus_RBar.setVisibility(View.VISIBLE);

        //mHandler.sendEmptyMessage(MESSAGE_VALID_NFCBUTTON);

        CrossIcReaderInfoBus crossIcReaderInfoBus = new CrossIcReaderInfoBus();
        crossIcReaderInfoBus.readWay = "NFC";
        crossIcReaderInfoBus.readCode = 0;
        crossIcReaderInfoBus.verifyWay = null;
        crossIcReaderInfoBus.verifyTs = 0;
        crossIcReaderInfoBus.msIcNo = null;
        EventBus.getDefault().post(crossIcReaderInfoBus);
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        // 构建[离线数据上报]事件通知...
        CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
        crossUploaderAuthInfoBus.uploaderWay = "NETWORK_CHANGE";
        crossUploaderAuthInfoBus.uploaderState = "notifyUploader";
        EventBus.getDefault().post(crossUploaderAuthInfoBus);
    }
}
