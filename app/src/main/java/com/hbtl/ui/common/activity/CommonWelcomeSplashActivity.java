package com.hbtl.ui.common.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.CrossLoadTokenInfoBus;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkStateService;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.menu.activity.MainMenuActivity;
import com.hbtl.utils.CommonUtils;
import com.hbtl.view.CirclePageIndicator;
import com.hbtl.view.ToastHelper;
import com.joanzapata.iconify.widget.IconTextView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


public class CommonWelcomeSplashActivity extends BaseActivity implements OnClickListener {

    @BindView(R.id.welcomeWeb_ViewPager) ViewPager welcomeWeb_ViewPager;
    @BindView(R.id.welcomePagerIndicator_CPI) CirclePageIndicator welcomePagerIndicator_CPI;
    @BindView(R.id.skipPager_Btn) Button skipPager_Btn;
    @BindView(R.id.nextPager_ITV) IconTextView nextPager_ITV;
    @BindView(R.id.donePager_Btn) Button donePager_Btn;
    @BindView(R.id.welcomeSplashLayout_LL) LinearLayout welcomeSplashLayout_LL;
    @BindView(R.id.welcomeLayout_RL) RelativeLayout welcomeLayout_RL;

    // 动态权限申请...
    RxPermissions rxPermissions;

    private Activity mActivity;
    private String loadTokenWay = null;// Token 加载方式 [create: 创建|refresh: 刷新|reuse: 复用] ...
    private boolean loadTokenLock = false;// 是否正在加载 Token ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Android 应用首次安装,未退出切换到后台情况下在桌面点应用图标,重复进入启动页(导致后续程序重复执行验证)问题
        // [Android应用Home键后Launcher重复启动问题](http://blog.csdn.net/zhangcanyan/article/details/52777265)
        // isTaskRoot() 判断当前Activity是否是第一个activity
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.app_welcome_splash_activity);
        ButterKnife.bind(this);
        mActivity = this;

        //注册事件监听器
        EventBus.getDefault().register(this);

        // 动态权限申请实例...
        rxPermissions = new RxPermissions(this);

        // Must be done during an initialization phase like onCreate
        rxPermissions
                .request(Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted) {
                        // Always true pre-M

                        // 读取本地设备编号...
                        int versionCode = CommonUtils.getVersionCode(this);
                        String deviceId = CommonUtils.getDeviceId(this);
                        //deviceId = "7808c901d7817fe2";//测试设备
                        //deviceId = "f419eaae523ff0db";//测试设备-带按键
                        //deviceId = "19a6e54461a5c0e8";//小米设备
                        //deviceId = "99000802716828";
                        //String deviceId = "99000802716824";
                        //String deviceId = "99000802352134";

                        Timber.i("[###][设备编号][deviceId: " + deviceId + "]");

                        CoamApplicationLoader.getInstance().appRunInfo.appVersion = versionCode;
                        CoamApplicationLoader.getInstance().appRunInfo.deviceId = deviceId;
                        CoamApplicationLoader.getInstance().syncRunPreferences("save");

                        // 加载 token ...
                        loadTokens();
                    } else {
                        Timber.w("Denied permission with ask never again");
                        // 手机权限[READ_PHONE_STATE]状态申请失败提示(NFC)...
                        ToastHelper.makeText(mActivity, "抱歉,软件需[NFC]权限,获取手持设备IMEI,请检查授权...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                    }
                });
    }

    // 加载授权认证 Token ...
    private void loadTokens() {
        // 如果已经在加载,则退出,防止重复加载...
        if (loadTokenLock) return;

        // 设定加载状态标签...
        loadTokenLock = true;

        // 初始化加载方式...
        loadTokenWay = null;

        // 设备注册...
        String token = CoamApplicationLoader.getInstance().appRunInfo.token;
        long tokenEts = CoamApplicationLoader.getInstance().appRunInfo.tokenEts;
        long cts = System.currentTimeMillis();
        // Token 最大可刷新间隔-两分钟
        long ctsEts = cts - tokenEts - CoamBuildVars.EKT_TOKEN_REFRESH_TS;
        // Token最大可用时长7天(检测6天)...
        long _ctsEts = cts - tokenEts - CoamBuildVars.EKT_TOKEN_CREATE_TS;
        if (token == null || _ctsEts > 0) {
            // 判断是否有网络连接(无网络连接取消 token 验证)...
            if (!NetworkStateService.isNetworkAvailable(mActivity)) {
                welcomeSplashLayout_LL.post(new Runnable() {
                    @Override
                    public void run() {
                        // 本地 Token 已过期,但强制更新Token时无网络问题...
                        ToastHelper.makeText(mActivity, "[#][抱歉,您的 Token 已过期,需要重新续签,请检查网络连接]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                    }
                });
                loadTokenLock = false;
                return;
            }

            if (BuildConfig.DEBUG) {
                welcomeSplashLayout_LL.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.makeText(mActivity, "[调试]_[正在重建更新 Token,请稍等]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                    }
                });
            }

            // 必须要创建新的 Token (强制刷新)...
            loadTokenWay = "create";

            // 发起设备注册请求...
            CommonUtils.getInstance().appDeviceRegister(mActivity);
        } else {
            // 如果当前网络状况良好,则尝试刷新 Token ...
            if (NetworkStateService.isNetworkAvailable(mActivity) && ctsEts > 0) {

                if (BuildConfig.DEBUG) {
                    welcomeSplashLayout_LL.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastHelper.makeText(mActivity, "[调试]_[正在重试刷新 Token,请稍等]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                        }
                    });
                }

                // 必须要创建新的 Token (非强制刷新)...
                loadTokenWay = "refresh";

                // 发起设备注册请求...
                CommonUtils.getInstance().appDeviceRegister(mActivity);
                return;
            }

            // 复用旧的 Token (复用)...
            loadTokenWay = "reuse";

            if (BuildConfig.DEBUG) {
                // 调试模式下显示注册 Token ...
                welcomeSplashLayout_LL.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastHelper.makeText(mActivity, "[调试]_复用设备已注册 [token: " + token + "],即将进入扫码程序...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                    }
                });
            }

            // 解决 ToastHelper 弹出 Activity 跳转问题[android.view.WindowLeaked: Activity com.hbtl.ui.common.activity.CommonWelcomeSplashActivity has leaked window android.widget.LinearLayout]
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    loadTokenLock = false;
                    startActivity(new Intent(CommonWelcomeSplashActivity.this, MainMenuActivity.class));
                    finish();
                }
            }, 2000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CrossLoadTokenInfoBus crossLoadTokenInfoBus) {
        if (crossLoadTokenInfoBus == null) return;

        // 更新Token更新标签...
        loadTokenLock = false;

        // DeviceRegister
        String loadWay = crossLoadTokenInfoBus.loadWay;
        String loadState = crossLoadTokenInfoBus.loadState;
        String authType = crossLoadTokenInfoBus.authType;
        String tokenType = crossLoadTokenInfoBus.tokenType;
        String token = crossLoadTokenInfoBus.token;

        switch (loadState) {
            case "success":
                // 解决 ToastHelper 弹出 Activity 跳转问题[android.view.WindowLeaked: Activity com.hbtl.ui.common.activity.CommonWelcomeSplashActivity has leaked window android.widget.LinearLayout]
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Timber.i("######@ Start Run Sub Handle...");

                        startActivity(new Intent(mActivity, MainMenuActivity.class));
                        finish();

                        Timber.i("End Run Sub Handle...");
                    }
                }, 2000);
                break;
            case "failed":
            case "error":
                // 更新获取 Token 失败问题
                ToastHelper.makeText(mActivity, "设备注册Token失败 [loadState: " + loadState + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();

                // 判断是否为可复用 Token...
                if (loadTokenWay.equals("refresh")) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Timber.i("######@ Start Run Sub Handle...");

                            startActivity(new Intent(mActivity, MainMenuActivity.class));
                            finish();

                            Timber.i("End Run Sub Handle...");
                        }
                    }, 2000);
                } else {
                    // TODO: 等待网络连接状态更新自动重连...
                    ToastHelper.makeText(mActivity, "设备注册失败,等待重连 [loadTokenWay: " + loadTokenWay + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                }
                break;
            default:
                // TODO: ...
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // 取消注册事件监听器(需要判定一下,防止未注册[LAUNCHER]重复启动finish()后未注册问题)
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.skipPager_Btn:
                //gotoLoginActivity();
                break;
            case R.id.donePager_Btn:
                //gotoLoginActivity();
            case R.id.nextPager_ITV:
                welcomeWeb_ViewPager.setCurrentItem(welcomeWeb_ViewPager.getCurrentItem() + 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void inNewIntent(@NotNull Intent intent) {
        // TODO ...
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        //@ 首先检查是否具备上传条件...
        String status = null;
        Timber.d("[#NetworkType@][networkType: " + networkType + "]");
        switch (networkType) {
            case NETWORK_WIFI:
                // TODO...

                // 发起设备注册请求...
                loadTokens();
                break;
            case NETWORK_2G:
            case NETWORK_3G:
            case NETWORK_4G:
                // TODO...

                if (BuildConfig.DEBUG) {
                    ToastHelper.makeText(mActivity, "[调试]_[恢复网络连接][正在刷新 Token,请稍等]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                }

                // 发起设备注册请求...
                loadTokens();
                break;
            case NETWORK_UNKNOWN:
            case NETWORK_NO:
                // TODO...
                status = "[#设备注册服务][appDeviceRegister(*)][当前网络不佳,设备注册失败]...";
                Timber.i(status);
                Toast.makeText(mActivity, status, Toast.LENGTH_SHORT).show();
                break;
            default:
                // TODO...
                status = "[#设备注册服务][appDeviceRegister(-)][未知网络状态,设备注册失败]...";
                Timber.i(status);
                Toast.makeText(mActivity, status, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}