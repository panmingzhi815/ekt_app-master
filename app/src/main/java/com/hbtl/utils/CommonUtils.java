package com.hbtl.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hbtl.api.CoamHttpService;
import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.AppDeviceInfo;
import com.hbtl.beans.CrossIcReaderInfoBus;
import com.hbtl.beans.CrossLoadTokenInfoBus;
import com.hbtl.beans.CrossUpdateEnterBus;
import com.hbtl.beans.CrossUploaderAuthInfoBus;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.ekt.R;
import com.hbtl.models.CommonAppRunInfo;
import com.hbtl.models.CommonAppUpdateInfo;
import com.hbtl.models.EnterAuthModel;
import com.hbtl.service.DownloadService;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.common.activity.CommonWebViewActivity;
import com.hbtl.ui.menu.activity.MainMenuActivity;
import com.hbtl.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class CommonUtils {

    // 参考单例设计模式
    private static volatile CommonUtils Instance = null;  // <<< 这里添加了 volatile

    public static CommonUtils getInstance() {
        CommonUtils inst = Instance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (CommonUtils.class) {
                inst = Instance;
                if (inst == null) {
                    inst = new CommonUtils();
                    Instance = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // 获取最前端运行的 AppTopActivity
    // [how to getTopActivity name or get current running application package name in lollipop?](https://stackoverflow.com/questions/28066231/how-to-gettopactivity-name-or-get-current-running-application-package-name-in-lo)
//    public static String getTopActivity(Context context) {
//        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//
//        String mPackageName = null;
//        if (Build.VERSION.SDK_INT > 20) {
//            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
//        } else {
//            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
//        }
//
//        return mPackageName;
//    }

    public static String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null) {
            return runningTaskInfos.get(0).topActivity.getClassName();
        } else {
            return null;
        }
    }

    // 跳转到网页
    public static void gotoWebActivity(Activity activity, String title, String website) {
        Intent intent = new Intent(activity, CommonWebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("website", website);
        activity.startActivity(intent);
    }

    public static void call(Context context, String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * @param sendWay  <br> AccountPasswordModify 修改密码
     *                 <br> AccountPhoneModify 修改手机号
     *                 <br> AccountPhoneRegister 手机注册
     * @param phoneNum
     */
    public static void getSecurityCode(final Context context, final String sendWay, final String phoneNum) {

        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("sendWay", sendWay);
        sParamList.put("VerifyPhone", phoneNum);

        CoamHttpService.getInstance(CoamHttpService.MPI_SERVER).getSecurityCode(sParamList)
                .subscribe();
    }

    public static String getStrFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //发送经纬度
    public static void sendPosition(final Context context, final double longitude, final double latitude) {
        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("lng", String.valueOf(longitude));
        sParamList.put("lat", String.valueOf(latitude));
//                    String result = HttpUtils.sendMainPost(context, CoamHttpService.SEND_PO, sParamList);

        // 暂时取消发送位置信息
        //CoamHttpService.getInstance().sendPo(sParamList).subscribe();
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String packageInviteInfo(String reason) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(CoamBuildVars.USER_IMAGE, CoamApplicationLoader.getInstance().appAccountInfo.aInfoPicture);
            jsonObject.put(CoamBuildVars.USER_NICK, CoamApplicationLoader.getInstance().appAccountInfo.aInfoName);
            jsonObject.put(CoamBuildVars.USER_INVITE_REASON, reason);
            return jsonObject.toString();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }


    public static void showNotification(Context context, String message) {
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.drawable.logo, message, System.currentTimeMillis());
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
//        notification.setLatestEventInfo(context, null, null, contentIntent);
//
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notificationManager.Notify(1, notification);
//        notificationManager.cancel(1);

        // 新更新
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        //参考 http://stackoverflow.com/questions/21294231/android-notification-cannot-resolve-method-build-from-abs-fragment
        //import android.support.v4.app.NotificationCompat;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        //mBuilder.setContentTitle( title );
        //mBuilder.setContentText( content );
        //mBuilder.setTicker("Ticker");


        //notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, mBuilder.build());
        mNotificationManager.cancel(1);
    }

    public static void showNotification(Context context, Class<?> cls, String message) {
        //NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();

        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        n.defaults = Notification.DEFAULT_ALL;

        n.icon = R.drawable.logo;
        n.when = System.currentTimeMillis();

        // Simply open the parent activity
        ///PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, cls), 0);

        // Change the name of the notification here
        ///n.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), message, pi);
        ///notificationManager.Notify(0, n);

        // 新更新
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, cls), 0);
        //参考 http://stackoverflow.com/questions/21294231/android-notification-cannot-resolve-method-build-from-abs-fragment
        //import android.support.v4.app.NotificationCompat;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentText(message);
        //mBuilder.setTicker("Ticker");

        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void showNotification(Context context, Class<?> cls, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();

        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        n.defaults = Notification.DEFAULT_ALL;

        n.icon = R.drawable.logo;
        n.when = System.currentTimeMillis();

        // Simply open the parent activity
        //PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, cls), 0);

        // Change the name of the notification here
        ///n.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), message, pi);
        ///notificationManager.Notify(0, n);

        // 新更新
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, cls), 0);
        //参考 http://stackoverflow.com/questions/21294231/android-notification-cannot-resolve-method-build-from-abs-fragment
        //import android.support.v4.app.NotificationCompat;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        mBuilder.setContentText(message);
        //mBuilder.setTicker("Ticker");

        mNotificationManager.notify(0, mBuilder.build());
    }

    // Display the topbar notification
    public static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification n = new Notification();

        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        n.flags |= Notification.FLAG_AUTO_CANCEL;

        n.defaults = Notification.DEFAULT_ALL;

        n.icon = R.drawable.logo;
        n.when = System.currentTimeMillis();

        // Simply open the parent activity
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainMenuActivity.class), 0);

        // Change the name of the notification here
        ///n.setLatestEventInfo(context, title, message, pi);
        ///notificationManager.Notify(0, n);


        // 新更新
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainMenuActivity.class), 0);
        //参考 http://stackoverflow.com/questions/21294231/android-notification-cannot-resolve-method-build-from-abs-fragment
        //import android.support.v4.app.NotificationCompat;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        //mBuilder.setTicker("Ticker");

        mNotificationManager.notify(0, mBuilder.build());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    // 获取状态栏的高度
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;
        return statusBarHeight;
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
            ComponentName componentName = runningTaskInfos.get(0).topActivity;
            String topActivityName = componentName.getClassName();
            String packageName = context.getPackageName();
            if (packageName != null && topActivityName != null && topActivityName.startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static String getDoubleDigit(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return String.valueOf(num);
    }

    // .......................................................................................................................
    // [获取APK当前签名文件的SHA1](http://lbs.amap.com/faq/top/hot-questions/253)
    public static String getApkSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 检查手机软件更新
    public void appBeatHeart() {
        final Map<String, String> sParamList = new HashMap<String, String>();
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        sParamList.put("deviceid", deviceId);

        CoamHttpService.getInstance().appBeatHeart(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppBeatHeartModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppBeatHeartModel> model) {
                        // 通过 EventBus 发送事件更新消息到 LoginWithRegisterActivity
                        //new EventBus().post(model);

                        Timber.e("#|@|IIIIIIIII[E-M]:");
                    }
                });
    }

    // Realm 数据库实例
    Realm mRealmInstance;
    private Subscription mSubscription;

    // 离线数据上报服务
    public void appGateDataReport() {
        long upTs = System.currentTimeMillis();
        Timber.d("[AGDR#离线数据上报服务][Start][CommonUtils.appGateDataReport(" + upTs + ")]................................................................");

        Context appContext = CoamApplicationLoader.getInstance().getApplicationContext();
        // 获取设备运行信息...
        AppDeviceInfo appDeviceInfo = CoamApplicationLoader.getInstance().appDeviceInfo;
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;

        //@ 首先检查是否具备上传条件...
        String status = null;

        // 判断是否已有数据上报任务[错开上传计划任务]...
        if (appDeviceInfo.lockAppAuthUploader) {
            status = "[调试]_[#数据上报服务][appGateDataReport][已有数据上报任务进行中]...";
            Timber.i(status);
            if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();
            return;
        }

        // 递减统计计数...
        if (appDeviceInfo.allowUpTs > 0) {
            CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs--;
            status = "[#数据上报服务][appGateDataReport][递减等待统计计数][allowUpTs: " + appDeviceInfo.allowUpTs + "]...";
            Timber.i(status);
            //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();

            // 构建[离线数据上报]事件通知...
            CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
            crossUploaderAuthInfoBus.uploaderWay = "AUTH_UPLOAD_DATA";
            crossUploaderAuthInfoBus.uploaderState = "waitingUploader";
            EventBus.getDefault().post(crossUploaderAuthInfoBus);

            return;
        }

        // CurrentActivityName...
        String topActivityName = CommonUtils.getTopActivity(appContext);
        Timber.d("[当前运行的 Activity][activity: " + topActivityName + "]");
        // [CommonScanQrCodeActivity|FaceDetectorActivity]关闭设备数据上报服务...
        if (topActivityName.equals("com.hbtl.ui.menu.activity.CommonScanQrCodeActivity") || topActivityName.equals("com.hbtl.ui.app.activity.FaceDetectorActivity")) {
            status = "[#数据上报服务][appGateDataReport][扫码|人脸识别]中...";
            Timber.i(status);
            //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();
            return;
        }

        // 检测当前设备网络状态...
        NetworkType networkType = CoamApplicationLoader.getInstance().appDeviceInfo.networkType;
        Timber.d("[#NetworkType@][networkType: " + networkType + "]");
        switch (networkType) {
            case NETWORK_WIFI:
                // TODO...
                break;
            case NETWORK_2G:
            case NETWORK_3G:
            case NETWORK_4G:
                // TODO...
                break;
            case NETWORK_UNKNOWN:
            case NETWORK_NO:
                // TODO...
                status = "[#数据上报服务][appGateDataReport(*)][当前网络不佳]...";
                Timber.i(status);
                //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();
                return;
            default:
                // TODO...
                status = "[#数据上报服务][appGateDataReport(-)][当前网络不佳]...";
                Timber.i(status);
                //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();
                return;
        }

        // 本地数据缓冲服务...
        mRealmInstance = Realm.getDefaultInstance();

        // 获取本地离线数据...
        final RealmResults<EnterAuthModel> enterAuthInfoList = mRealmInstance.where(EnterAuthModel.class).findAll();

        status = "[#数据上报服务][appGateDataReport(+)][开启数据上报服务]...";
        Timber.i(status);
        //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();

        // 无离线数据,则返回...
        if (enterAuthInfoList.size() == 0) {
            status = "[#数据上报服务][appGateDataReport(+)][暂无数据上报数据]...";
            Timber.i(status);
            //if (BuildConfig.DEBUG) Toast.makeText(appContext, status, Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建[离线数据上报]事件通知...
        CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
        crossUploaderAuthInfoBus.uploaderWay = "AUTH_UPLOAD_DATA";
        crossUploaderAuthInfoBus.uploaderState = "startUploader";
        EventBus.getDefault().post(crossUploaderAuthInfoBus);

        // 离线数据单次上报量
        int oUpDn = CoamApplicationLoader.getInstance().appRunInfo.oUpDn;
        if (BuildConfig.DEBUG)
            Toast.makeText(appContext, "[调试]_本次离线上传数据配额[oUpDn: " + oUpDn + "]...", Toast.LENGTH_SHORT).show();
        //oUpDn = 1;

        Timber.i("[列举离线数据上报任务]...");
        List<EnterAuthModel> upEnterAuthDataList = new ArrayList<>();
        for (EnterAuthModel enterAuthInfo : enterAuthInfoList) {
            Timber.i("[EnterAuthModel][@Loop Read@][qrId: " + enterAuthInfo.qrId + "][authWay: " + enterAuthInfo.authWay + "][qrCardSn: " + enterAuthInfo.qrCardSn + "][qrCardTs: " + enterAuthInfo.qrCardTs + "][authEnterDts: " + enterAuthInfo.authEnterDts + "][qrCode: " + enterAuthInfo.qrCode + "]");
            // 限定追加上报离线数据...
            if (upEnterAuthDataList.size() < oUpDn) upEnterAuthDataList.add(enterAuthInfo);
        }

        // 加密上传数据...
        JSONArray jsonArray = enAuthDataList(upEnterAuthDataList);

        //Timber.i("[###]-[enAuthInfo: " + enAuthInfo + "]...");

        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("versionno", String.valueOf(getVersionCode(appContext)));
        sParamList.put("deviceid", deviceId);
        sParamList.put("status", "1");
        sParamList.put("timestamp", String.valueOf(upTs));
        sParamList.put("idcard", "0");
        sParamList.put("datetype", "1");// 使用离线数据处理服务...
        sParamList.put("extrainfo", jsonArray.toString());

        CoamHttpService.getInstance().appGateDataReport(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppGateDataReportModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        Timber.e("#|@|IIIIIIIII[C-M]:");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();

                        // 构建[离线数据上报]事件通知...
                        CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
                        crossUploaderAuthInfoBus.uploaderWay = "AUTH_UPLOAD_DATA";
                        crossUploaderAuthInfoBus.uploaderState = "uploaderError";
                        EventBus.getDefault().post(crossUploaderAuthInfoBus);
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppGateDataReportModel> model) {

                        // if(true)...
                        // 依次获取上传实例...
                        for (EnterAuthModel upEnterAuthData : upEnterAuthDataList) {

                            // 获取本地离线图片地址...
                            String captureFacePath = upEnterAuthData.captureImgFn;

                            // 人脸识别完成,清除离线数据...
                            mRealmInstance.beginTransaction();
                            upEnterAuthData.deleteFromRealm();
                            mRealmInstance.commitTransaction();

                            // 删除本地离线缓存图片文件...
                            FileUtils.deleteFile(appContext, captureFacePath);
                        }

                        CoamApplicationLoader.getAppHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 构建[离线数据上报]事件通知...
                                CrossUploaderAuthInfoBus crossUploaderAuthInfoBus = new CrossUploaderAuthInfoBus();
                                crossUploaderAuthInfoBus.uploaderWay = "AUTH_UPLOAD_DATA";
                                crossUploaderAuthInfoBus.uploaderState = "uploaderSuccess";
                                EventBus.getDefault().post(crossUploaderAuthInfoBus);
                            }
                        }, 200);

                        Timber.e("#|@|IIIIIIIII[N-M]:");
                    }
                });

        Timber.d("[AGDR#离线数据上报服务][End][CommonUtils.appGateDataReport(" + upTs + ")]__________________________________________________________________");
    }

    // 加密上报离线数据...
    public JSONArray enAuthDataList(List<EnterAuthModel> upEnterAuthDataList) {

        long upTs = System.currentTimeMillis();
        // 获取设备运行信息...
        CommonAppRunInfo appRunInfo = CoamApplicationLoader.getInstance().appRunInfo;
        String deviceId = appRunInfo.deviceId;

        // Json 序列化...
        JSONArray jsonArray = new JSONArray();

        // 依次获取上传实例...
        for (EnterAuthModel upEnterAuthData : upEnterAuthDataList) {
            //EnterAuthModel upEnterAuthInfo = enterAuthDataList.first();
            Timber.i("[上报离线数据任务]...");
            Timber.i("[EnterAuthModel][@Loop Read@][qrId: " + upEnterAuthData.qrId + "][authWay: " + upEnterAuthData.authWay + "][qrCardTs: " + upEnterAuthData.qrCardTs + "][authEnterDts: " + upEnterAuthData.authEnterDts + "][qrCode: " + upEnterAuthData.qrCode + "]");

            // 获取离线图片...
            String qrCode = upEnterAuthData.qrCode;
            String captureFacePath = upEnterAuthData.captureImgFn;
            Bitmap bitmap = ImageUtils.getSmallBitmap(captureFacePath);
            //myTakeAvatar_IV.setImageBitmap(bitmap);
            String facedata = ImageUtils.bitmapToBase64(bitmap);

            // 提取特征码,00为微官网,01为小程序
            String enterCode = qrCode.substring(8, 10);

            // 生成签名的字符串...
            LinkedHashMap<String, String> sQueryParams = new LinkedHashMap<String, String>();
            sQueryParams.put("cardsn", upEnterAuthData.qrCardSn);
            sQueryParams.put("deviceid", deviceId);
            sQueryParams.put("feturecode", enterCode);
            sQueryParams.put("pictype", "1");
            // 离线验证时间与二维码生成时间保持一致...
            sQueryParams.put("timestamp", String.valueOf(upEnterAuthData.qrCardTs));
            sQueryParams.put("key", appRunInfo.aesKey);
            String usAuthInfo = mapToQueryString(sQueryParams);

            String md5AuthInfo = Md5Utils.getMD5(usAuthInfo);
            md5AuthInfo = md5AuthInfo.toUpperCase();
            Timber.i("[#AES#]加密序列串[md5AuthInfo:" + md5AuthInfo + "]...");

            HmacUtil hmac = new HmacUtil();
            //md5QrInfo = "BA6DE3256AE58FD09F461AF932E1A337";
            String hmacAuthInfo = hmac.stringToSign(md5AuthInfo);
            hmacAuthInfo = hmacAuthInfo.toUpperCase();
            Timber.i("[#AES#]加密签名后[hmacAuthInfo:" + hmacAuthInfo + "]");

            Timber.i("[###]-[usAuthInfo: " + usAuthInfo + "]...");
            Timber.i("[###]-[hmacAuthInfo: " + hmacAuthInfo + "]...");

            // 生成加密数据的字符串...
            LinkedHashMap<String, String> pQueryParams = new LinkedHashMap<String, String>();
            pQueryParams.put("cardsn", upEnterAuthData.qrCardSn);
            pQueryParams.put("deviceid", deviceId);
            pQueryParams.put("facedata", facedata);
            pQueryParams.put("feturecode", enterCode);
            pQueryParams.put("pictype", "1");
            // 离线验证时间与二维码生成时间保持一致...
            pQueryParams.put("timestamp", String.valueOf(upEnterAuthData.qrCardTs));
            pQueryParams.put("sign", hmacAuthInfo);
            String upAuthInfo = mapToQueryString(pQueryParams);

            //Timber.i("[###]-[upAuthInfo: " + upAuthInfo + "]...");

            String enAuthInfo = null;
            try {
                //PublicKey publicKey = RsaUtils.loadPublicKey(CoamBuildVars._publicCaKey);
                //enAuthInfo = RsaUtils.encryptDataToStr(upEnterAuthInfo.qrCode.getBytes(), publicKey);
                // 7ySqEVIWuCrzP6fY6P72NQ==
                enAuthInfo = AesUtils.encrypt(upAuthInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            jsonArray.put(enAuthInfo);
        }

        return jsonArray;
    }

    // AppPing
    public void appGatePing() {
        final Map<String, String> sParamList = new HashMap<String, String>();
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        long timestamp = System.currentTimeMillis();// / 1000;
        sParamList.put("deviceid", deviceId);
        sParamList.put("timestamp", String.valueOf(timestamp));

        CoamHttpService.getInstance().appGatePing(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppGatePingModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppGatePingModel> model) {
                        // 通过 EventBus 发送事件更新消息到 LoginWithRegisterActivity
                        //new EventBus().post(model);

                        Timber.e("#|@|IIIIIIIII[E-M]:");
                    }
                });
    }

    // 更新 App 密钥
    public void appLoadEncryptKey(Activity activity) {

        // 判断是否为有效 key ...
        //String aesKey = CoamApplicationLoader.getInstance().appRunInfo.aesKey;
        //long aesEts = CoamApplicationLoader.getInstance().appRunInfo.aesEts;
        //long cts = System.currentTimeMillis();
        //long ctsEts = cts - aesEts - 10 * 60 * 1000;// 十分钟
        //if (aesKey == null) {// || ctsEts > 0
        //    return;
        //}

        final Map<String, String> sParamList = new HashMap<String, String>();
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        long timestamp = System.currentTimeMillis();// / 1000;
        sParamList.put("deviceid", deviceId);
        sParamList.put("timestamp", String.valueOf(timestamp));

        CoamHttpService.getInstance().appLoadEncryptKey(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppLoadEncryptKeyModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppLoadEncryptKeyModel> model) {
                        // 通过 EventBus 发送事件更新消息到 LoginWithRegisterActivity
                        //new EventBus().post(model);

                        String aesKey = model.re_data.aesKey;
                        String rsaPublicKey = model.re_data.rsaPublicKey;

                        CoamApplicationLoader.getInstance().appRunInfo.aesKey = aesKey;
                        CoamApplicationLoader.getInstance().appRunInfo.rsaPublicKey = rsaPublicKey;
                        CoamApplicationLoader.getInstance().syncRunPreferences("save");

                        String info = "设备Key更新成功[aesKey: " + aesKey + "][rsaPublicKey: " + rsaPublicKey + "]...";
                        //if (BuildConfig.DEBUG) ToastHelper.makeText(activity, info, ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                        Timber.i("#|@|IIIIIIIII[E-M]:[info: " + info + "]");
                    }
                });
    }

    // 设备注册...
    public void appDeviceRegister(Activity activity) {
        //ToastHelper.makeText(mActivity, "正在设备注册,请稍候稍等...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();

        Context appContext = CoamApplicationLoader.getInstance().getApplicationContext();
        int versionCode = CommonUtils.getVersionCode(appContext);

        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("versionno", String.valueOf(versionCode));
        sParamList.put("deviceid", deviceId);

        CoamHttpService.getInstance().appDeviceRegister(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppDeviceRegisterModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();

                        String info = "设备注册异常,请稍候尝试...";
                        ToastHelper.makeText(activity, info, ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();

                        // 发送事件总线消息...
                        CrossLoadTokenInfoBus crossLoadTokenInfoBus = new CrossLoadTokenInfoBus();
                        crossLoadTokenInfoBus.loadWay = "DeviceRegister";
                        crossLoadTokenInfoBus.loadState = "error";
                        crossLoadTokenInfoBus.authType = null;
                        crossLoadTokenInfoBus.tokenType = null;
                        crossLoadTokenInfoBus.token = null;
                        EventBus.getDefault().post(crossLoadTokenInfoBus);

                        Timber.i("[info: " + info + "]");
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppDeviceRegisterModel> model) {

                        // 发送事件总线消息...
                        CrossLoadTokenInfoBus crossLoadTokenInfoBus = new CrossLoadTokenInfoBus();
                        crossLoadTokenInfoBus.loadWay = "DeviceRegister";

                        int status = model.re_code;
                        switch (status) {
                            case 0:// 设备注册成功
                                int beathearPeriod = model.re_data.beathearPeriod;
                                int tourId = model.re_data.tourId;
                                String token = model.re_data.token;
                                long expires = model.re_data.expires;
                                expires = System.currentTimeMillis();

                                // 更新本地 token ...
                                CoamApplicationLoader.getInstance().appRunInfo.token = token;
                                CoamApplicationLoader.getInstance().appRunInfo.tokenEts = expires;
                                CoamApplicationLoader.getInstance().syncRunPreferences("save");

                                // 配置授权信息...
                                crossLoadTokenInfoBus.loadState = "success";
                                crossLoadTokenInfoBus.authType = "Bearer";
                                crossLoadTokenInfoBus.tokenType = "verify";
                                crossLoadTokenInfoBus.token = token;

                                String info = "设备注册成功,即将进入主界面...";
                                ToastHelper.makeText(activity, info, ToastHelper.LENGTH_LONG, ToastHelper.ToastType.SUCCESS).show();
                                Timber.i("[info: " + info + "]");
                                break;
                            case CoamHttpService.Code_DeviceAlreadyRegister:// 设备已注册.
                                // 配置授权信息...
                                crossLoadTokenInfoBus.loadState = "failed";
                                crossLoadTokenInfoBus.authType = null;
                                crossLoadTokenInfoBus.tokenType = null;
                                crossLoadTokenInfoBus.token = null;

                                // 设备频繁注册获取 Token 失败反馈...
                                ToastHelper.makeText(activity, "设备已注册[status: " + status + "],未获取token...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                                break;
                            case CoamHttpService.Code_DeviceNoExist:// 设备不存在.
                                // 配置授权信息...
                                crossLoadTokenInfoBus.loadState = "failed";
                                crossLoadTokenInfoBus.authType = null;
                                crossLoadTokenInfoBus.tokenType = null;
                                crossLoadTokenInfoBus.token = null;

                                // 设备编号不存在错误反馈
                                ToastHelper.makeText(activity, "设备不存在[status: " + status + "],未获取token...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                                break;
                            default:
                                // 未知设备注册响应码...
                                ToastHelper.makeText(activity, "未知设备注册响应码,操作失败...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                                break;
                        }

                        // 发送事件总线消息...
                        EventBus.getDefault().post(crossLoadTokenInfoBus);

                        Timber.i("#|@|IIIIIIIIII[I-M]:" + model.toString());
                    }
                });
    }

    // 身份证编号查询...
    public void appCheckIdCode(Activity activity, String icCode, long icVerifyTs) {
        Context appContext = CoamApplicationLoader.getInstance().getApplicationContext();
        int versionCode = CommonUtils.getVersionCode(appContext);

        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("versionno", String.valueOf(versionCode));
        sParamList.put("deviceid", deviceId);
        sParamList.put("icCode", icCode);

        CoamHttpService.getInstance().checkIdCode(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsCheckIdCodeModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();

                        String info = "[调试]_卡片编号缓存检索失败,请稍候尝试...";
                        if (BuildConfig.DEBUG) ToastHelper.makeText(activity, info, ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();

                        // 检卡消息通知...
                        CrossIcReaderInfoBus crossIcReaderInfoBus = new CrossIcReaderInfoBus();

                        // 重建刷卡时间通知...
                        crossIcReaderInfoBus.readWay = "NFC";
                        crossIcReaderInfoBus.readCode = CoamHttpService.IdCardErrorCode;
                        crossIcReaderInfoBus.verifyWay = "msp";
                        crossIcReaderInfoBus.verifyTs = icVerifyTs;
                        crossIcReaderInfoBus.msIcNo = null;

                        EventBus.getDefault().post(crossIcReaderInfoBus);

                        Timber.e("[info: " + info + "]");
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsCheckIdCodeModel> model) {

                        int status = model.re_code;
                        String re_desc = model.re_desc;
                        String icNo = model.re_data.icNo;

                        if (BuildConfig.DEBUG) ToastHelper.makeText(activity, "[调试]_[re_desc: " + re_desc + "]", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

                        // 检卡消息通知...
                        CrossIcReaderInfoBus crossIcReaderInfoBus = new CrossIcReaderInfoBus();

                        // 重建刷卡时间通知...
                        crossIcReaderInfoBus.readWay = "NFC";
                        crossIcReaderInfoBus.readCode = status;
                        crossIcReaderInfoBus.verifyWay = "msp";
                        crossIcReaderInfoBus.verifyTs = icVerifyTs;
                        crossIcReaderInfoBus.msIcNo = icNo;

                        // 发送事件总线消息...
                        EventBus.getDefault().post(crossIcReaderInfoBus);

                        Timber.i("#|@|IIIIIIIIII[I-M]:" + model.toString());
                    }
                });
    }

    // 查询 App 有效入园次数
    public void appQueryEnterCount() {
        Context appContext = CoamApplicationLoader.getInstance().getApplicationContext();
        int versionCode = CommonUtils.getVersionCode(appContext);

        final Map<String, String> sParamList = new HashMap<String, String>();
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        long timestamp = System.currentTimeMillis();
        sParamList.put("versionno", String.valueOf(versionCode));
        sParamList.put("deviceid", deviceId);
        sParamList.put("querytype", "1");
        sParamList.put("timestamp", String.valueOf(timestamp));

        CoamHttpService.getInstance().appQueryEnterCount(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsAppQueryEnterCountModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsAppQueryEnterCountModel> model) {
                        // 通过 EventBus 发送事件更新消息到 LoginWithRegisterActivity

                        String date = model.re_data.date;
                        int enterCount = model.re_data.enterCount;

                        CrossUpdateEnterBus crossUpdateEnterBus = new CrossUpdateEnterBus();
                        crossUpdateEnterBus.updateWay = "common";
                        crossUpdateEnterBus.enterCount = enterCount;

                        EventBus.getDefault().post(crossUpdateEnterBus);
                        Timber.i("#|@|IIIIIIIII[I-M]:");
                    }
                });
    }

    // 解码二维码信息...
    // ekatong@007GlNR17MAbU+PippC6OmW9kfWC5U9X7/pCIgfmnixixG0lbYX8kSxsovxTeFzp5QDI4rPIiGFZ1EpnkX6VkOIv4xpqw16fSOdsTLzC6roNs=
    public EnterAuthModel loadQrCodeInfo(Activity activity, String qrCode) {
        Timber.i("[qrCode: " + qrCode + "]...");

        String[] qrList = qrCode.split("@");
        String qrTag = qrList[0];
        if (!qrTag.equals("ekatong")) {
            // 未知二维码响应...
            ToastHelper.makeText(activity, "二维码验证失败,未知标签[qrTag: " + qrTag + "]", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
            return null;
        }
        String qrDetail = qrList[1];
        // 提取特征码,00为微官网,01为小程序
        String enterCode = qrDetail.substring(0, 2);
        // _qrCodeInfo="KmYXFAfTACU2FQNokNkb+S3fFFI/0j8Zk0htk/2Zz4sNGDdSUlLYcwXr+9HmNk4NcLgOzNJqsOUeYGGCr8fqUi30vO89cC4SN5F+1qX8t0g="
        // qrCodeInfo=tlnkw8LcpNAj1Sxn1LNJ|1510912648|176FA00C6BEE6702076E9F21A88E5E89AAC3CAEA
        String _qrCodeInfo = qrDetail.substring(2);

        Timber.i("[qrTag: " + qrTag + "][enterCode: " + enterCode + "][_qrCodeInfo: " + _qrCodeInfo + "]");

        String qrCodeInfo = null;
        try {
            // decrypt（直接解密测试）...

            // AES_128_ECB
            //_qrCodeInfo = "7GlNR17MAbU+PippC6OmW5KDieKGUkZ3rsgs2P+DMP1GUZAYbb4hT5B/36Jlhxe2uFyn10NFdIsopebzXRblDVvuyNXsq4XwHX89Tp9w6/U=";
            // AES_192_ECB
            //_qrCodeInfo = "mAp9JsVprbYuhLxG8IZyB/SbkdZ1ioQyTrraNPy8R2pPtxYG24assbCfH/eqFj4yWfUeT69p7SocLde1zRuHhSBdOVcBZDeHZpUf/QiGZno=";
            // AES_256_ECB
            //_qrCodeInfo = "Dglz9N/lGRb1Bt+fcIwO2Xtzoyzf979kvz5MmI68y5xajLgzXp8dBw5B4VKqX5UEcgBvH3rxk8XKvaJE9pM5E5G6nnUa9qmxn9+0VB2NisA=";

            // AES_128_CBC
            //_qrCodeInfo = "oSUIVLZEuxeYKD0glq/HC0c5FtRzTkYA487LkLhw+vOofROt7sEPqSON6YFa+zJyZEw3Uf2eM79MqO1VWtFHC2i3RT5CINm4GOE6V9BNR8U=";
            // AES_192_CBC
            //_qrCodeInfo = "b0LbHOWVIyYBTeNdC86iVsNiDQcOxt2Dipoba5hf4TUSZJZZlOKXmB3rejA0UWuS9bOOh2TPeWYGWEtf8JlHL0gkwA6FOdXQtuhsMo4xwWE=";
            // AES_256_CBC
            //_qrCodeInfo = "DMTZiYAAYRE6jPx0GhgbQ2C7bYoOpRd4AI6wReZx5zrMk4k+p6BDLDcW1YhdwBOh0ed1x8V7A+hGjnSOjbNk413bqM4s8HqNj5VJKxU0s8g=";

            // ekatong@00
            //_qrCodeInfo = "7GlNR17MAbU+PippC6OmW+dfWM8HVWsrhP+lI1tCGiGdacA+RJVz99gfkRLxuModRs5Zyz6ufLQuLN9r8/49//6oBVs6bckqOgjXPAaH5Sg=";

            qrCodeInfo = AesUtils.decrypt(_qrCodeInfo);
            Timber.d("[#AES#]->解密测试[" + qrCodeInfo + "]");
        } catch (Exception e) {
            e.printStackTrace();
            // 如果非加密串,则解密会报异常...
            Timber.d("ERROR:" + e.getMessage());
            return null;
        }
        Timber.i("[qrCodeInfo: " + qrCodeInfo + "]");

        String[] qrCodeList = qrCodeInfo.split("\\|");
        if (qrCodeList.length != 3) {
            Context appContext = CoamApplicationLoader.getInstance().getApplicationContext();
            Toast.makeText(appContext, "二维码格式验证失败,二维码(解码)长度不匹配[qrCodeList.length: " + qrCodeList.length + "]", Toast.LENGTH_LONG).show();
            Timber.i("[二维码验签格式不合法]...");
            return null;
        }

        // 获取二维码信息...
        String qrCardSn = qrCodeList[0];
        String qrCardTs = qrCodeList[1];
        String qrCardSign = qrCodeList[2];

        Timber.i("#[解码后]@[qrCardSn: " + qrCardSn + "][qrCardTs: " + qrCardTs + "][qrCardSign: " + qrCardSign + "]");

        EnterAuthModel enterAuthInfo = new EnterAuthModel();
        enterAuthInfo.qrCardSn = qrCardSn;
        enterAuthInfo.qrCardTs = Long.parseLong(qrCardTs);
        enterAuthInfo.qrCardSign = qrCardSign;

        return enterAuthInfo;
    }

    // 验证二维码签名...
    // tlnkw8LcpNAj1Sxn1LNJ|1510912648|176FA00C6BEE6702076E9F21A88E5E89AAC3CAEA
    public boolean vsQrCodeInfo(EnterAuthModel enterAuthInfo) {

        // 判断输入参数...
        if (enterAuthInfo == null) return false;

        // 加解密...
        String qrAesKey = CoamApplicationLoader.getInstance().appRunInfo.aesKey;

        String qrCardSn = enterAuthInfo.qrCardSn;
        long qrCardTs = enterAuthInfo.qrCardTs;
        String qrCardSign = enterAuthInfo.qrCardSign;
        String aseQrInfo = "cardsn=" + qrCardSn + "&timestamp=" + qrCardTs + "&key=" + qrAesKey;

        //Timber.i("[#AES#]原始串[qrCodeInfo: " + qrCodeInfo + "]");
        //Uri.Builder builder = new Uri.Builder();
        //builder.clearQuery().appendQueryParameter("cardsn", qrCardSn).appendQueryParameter("timestamp", qrTs).appendQueryParameter("key", qrAesKey);
        //String _aseQrInfo = builder.build().toString();
        //Timber.i("[#]=>[_aseQrInfo: " + _aseQrInfo + "]");
        Timber.i("[#AES#]加密拼接串[aseQrInfo:" + aseQrInfo + "]...");

        String md5QrInfo = Md5Utils.getMD5(aseQrInfo);
        md5QrInfo = md5QrInfo.toUpperCase();
        Timber.i("[#AES#]加密序列串[md5QrInfo:" + md5QrInfo + "]...");

        HmacUtil hmac = new HmacUtil();
        //md5QrInfo = "BA6DE3256AE58FD09F461AF932E1A337";
        String hmacQrInfo = hmac.stringToSign(md5QrInfo);
        hmacQrInfo = hmacQrInfo.toUpperCase();
        Timber.i("[#AES#]加密签名后[hmacQrInfo:" + hmacQrInfo + "]");

        // 判断签名是否一致...
        if (!qrCardSign.equals(hmacQrInfo)) {
            Timber.i("[二维码验签失败]...");
            return false;
        }

        Timber.i("[二维码验签通过]...");

        return true;
    }

    // 增加离线数据上传间隔时长...
    // [wTs:等待间隔|mwTs:最大等待间隔长度]...
    public static void addLoopWaitUpTs(int wTs, int mwTs) {
        CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs += wTs;
        // 测试用-最大间隔时长限制为 1 分钟...
        if (BuildConfig.DEBUG) mwTs = 6;
        if (CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs > mwTs) {
            CoamApplicationLoader.getInstance().appDeviceInfo.allowUpTs = mwTs;
        }
    }

    // 检查手机软件更新...
    public void checkAppUpdate(Activity activity) {
        Context appContext = CoamApplicationLoader.appContextInstance;

        final Map<String, String> sParamList = new HashMap<String, String>();
        int appVersion = CoamApplicationLoader.getInstance().appRunInfo.appVersion;
        String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
        sParamList.put("versionno", String.valueOf(appVersion));
        sParamList.put("deviceid", deviceId);

        CoamHttpService.getInstance().checkAppUpdate(sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel> model) {
                        switch (model.re_code) {
                            case 203:// 软件有新版本,需要升级
                                int currentCode = CommonUtils.getVersionCode(appContext);
                                if (model.re_data.versionNo > currentCode) {
                                    // 软件更新提示
                                    ToastHelper.makeText(appContext, "即将更新版本", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();

                                    String upgradeInfo = model.re_desc;
                                    new MaterialDialog.Builder(activity)
                                            .title("E卡通版本更新")
                                            .content(upgradeInfo)
                                            .positiveText("下载")
                                            .negativeText("取消")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                                    // 设置应用默认测试下载地址...
                                                    model.re_data.downloadUrl = "https://qs-mo.coam.co/ekt-17.apk";
                                                    CommonUtils.getInstance().startDownloadService(appContext, model.re_data.downloadUrl);
                                                }
                                            })
                                            .show();
                                } else {
                                    // 软件更新反馈
                                    ToastHelper.makeText(appContext, "已是最新版本", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.SUCCESS).show();
                                }
                                break;
                            case 204:// 无新版本
                                ToastHelper.makeText(appContext, "无新版本", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();
                                break;
                            case -199:// token 错误
                                ToastHelper.makeText(appContext, "[-199] token 错误", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.ERROR).show();
                                break;
                            default:
                                // 未知响应状态码
                                ToastHelper.makeText(appContext, "检测更新,未知状态码[re_code: " + model.re_code + "]", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.WARNING).show();
                                break;
                        }

                        // 如果返回有正确的结果,则更新本地离线数据上报配量...
                        if (Arrays.asList(new Integer[]{203, 204}).contains(model.re_code)) {
                            CoamApplicationLoader.getInstance().appRunInfo.oUpDn = model.re_data.oUpDn;
                            CoamApplicationLoader.getInstance().syncRunPreferences("save");
                        }

                        Timber.i("#|@|IIIIIIIII[I-M]:");
                    }
                });
    }

    // [Download Service].......................................................................................................................
    private CommonAppUpdateInfo updateInfo;

    private MaterialDialog updateDialog;

    public void updateDownloadingProcess(int pro) {
        if (updateDialog != null) {
            updateDialog.setProgress(pro);
        }
    }

    public void updateDownloadingSuccess(Context context, File file) {
        if (updateDialog != null) {
            updateDialog.dismiss();
        }
        installApk(context, file);
    }

    public void updateDownloadingFail() {

    }

    public void startDownloadService(Context context, String downloadUrl) {
        if (DownloadService.getInstance() != null && DownloadService.getInstance().getFlag() != DownloadService.Flag_Init) {
            // 已经在下载
            ToastHelper.makeText(context, "已经在下载", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.WARNING).show();
            return;
        }

        updateDialog = new MaterialDialog.Builder(context)
                .title(context.getResources().getString(R.string.app_name))
                .content("正在下载...")
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, false).build();
//        if (CommonUtils.getVersionCode(context) < updateInfo.compatibleCode) {
//            updateDialog.setCancelable(false);
//        } else {
//            updateDialog.setCancelable(true);
//        }
        updateDialog.setCancelable(true);
        updateDialog.show();

        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("flag", "start");
        intent.putExtra("url", downloadUrl);
        String filePath = File.separator + "shangyun" + File.separator + "download" + File.separator;
        intent.putExtra("filePath", filePath);
        intent.putExtra("fileName", "shangyun.apk");
        context.startService(intent);
    }

    public void pauseDownloadService(Context context) {
        String flag = null;
        int f = DownloadService.getInstance().getFlag();
        if (DownloadService.getInstance() != null) {
            // 如果当前已经暂停,则恢复
            if (f == DownloadService.Flag_Pause) {
                flag = "resume";
            } else if (f == DownloadService.Flag_Down) {
                flag = "pause";
            } else {
                return;
            }
        }
        Intent it = new Intent(context, DownloadService.class);
        it.putExtra("flag", flag);
        context.startService(it);
    }

    // 需要手动关闭服务,防止系统杀死前台进程后后台服务一直报错
    // [onStartCommand][Unable to start service with null: java.lang.NullPointerException](http://blog.csdn.net/u010037124/article/details/38469921)
    public void stopDownloadService(Context context) {
        Intent it = new Intent(context, DownloadService.class);
        it.putExtra("flag", "stop");
        context.startService(it);
    }

    // 安装 apk
    public static void installApk(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    // .....................................................................................................................

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String mapToQueryString(Map<String, String> map) {
        StringBuilder string = new StringBuilder();

//        if (map.size() > 0) {
//            string.append("?");
//        }

        for (Map.Entry<String, String> entry : map.entrySet()) {

            // 如果已有字符串...
            if (string.length() != 0) string.append("&");

            string.append(entry.getKey());
            string.append("=");
            string.append(entry.getValue());
        }

        return string.toString();
    }

}
