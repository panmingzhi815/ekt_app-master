package com.hbtl.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.CrossNwUpdateInfoBus;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * Created by yzhang on 2017/11/24.
 * [Android 动态监听网络变化](http://www.jianshu.com/p/6e2bc3e58f88)
 */

public class NetworkStateService extends Service {
    // Class that answers queries about the state of network connectivity.
    // 系统网络连接相关的操作管理类.

    private ConnectivityManager connectivityManager;
    // Describes the status of a network interface.
    // 网络状态信息的实例
    private NetworkInfo networkInfo;

    /**
     * 当前处于的网络
     * 0 :null
     * 1 :2G/3G
     * 2 :wifi
     */
    public static int networkStatus;

    //public static final String NETWORKSTATE = "com.text.android.network.state"; // An action name
    public static final String NETWORKSTATE = "com.hbtl.service.NetworkStateService"; // An action name

    // 广播实例
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // The action of this intent or null if none is specified.
            // action是行动的意思,也许是我水平问题无法理解为什么叫行动,我一直理解为标识（现在理解为意图）
            String action = intent.getAction(); //当前接受到的广播的标识(行动/意图)

            // 当当前接受到的广播的标识(意图)为网络状态的标识时做相应判断
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Timber.d("[NetworkStateService][-.-.-]");

                // 获取网络连接管理器
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // 获取当前网络状态信息
                networkInfo = connectivityManager.getActiveNetworkInfo();

                NetworkType networkType = null;
                if (networkInfo != null && networkInfo.isAvailable()) {
                    //当NetworkInfo不为空且是可用的情况下,获取当前网络的Type状态
                    //根据NetworkInfo.getTypeName()判断当前网络
                    String name = networkInfo.getTypeName();
                    Timber.d("[NetworkTypeName: " + name + "]...");

                    //更改NetworkStateService的静态变量,之后只要在Activity中进行判断就好了
                    if (name.equals("WIFI")) {
                        networkStatus = 2;
                        Timber.d("[NetworkStateService][WIFI]");
                        networkType = NetworkType.NETWORK_WIFI;
                    } else if (name.equalsIgnoreCase("MOBILE")) {
                        networkStatus = 1;
                        Timber.d("[NetworkStateService][*G]");
                        networkType = NetworkType.NETWORK_4G;

                        // [android如何判断当前网络类型（联网,2g,3g,wifi等）](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/0607/1621.html)
                        //String proxyHost = android.net.Proxy.getDefaultHost();
                        //networkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NetworkType.NETWORK_3G : NetworkType.NETWORK_2G) : NETWORKTYPE_WAP;
                    }

                } else {

                    // NetworkInfo为空或者是不可用的情况下
                    networkStatus = 0;
                    Timber.d("[NetworkStateService][No]");
                    networkType = NetworkType.NETWORK_NO;

                    Toast.makeText(context, "没有可用网络!\n请连接网络后刷新本界面", Toast.LENGTH_SHORT).show();

                    Intent it = new Intent();
                    it.putExtra("networkStatus", networkStatus);
                    it.setAction(NETWORKSTATE);
                    sendBroadcast(it); //发送无网络广播给注册了当前服务广播的Activity

                    /**
                     * 这里推荐使用本地广播的方式发送:
                     * LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                     */
                }

                // 更新网络状态...
                CoamApplicationLoader.getInstance().appDeviceInfo.networkType = networkType;

                // 构建网络状态服务事件通知...
                CrossNwUpdateInfoBus crossNwUpdateInfoBus = new CrossNwUpdateInfoBus();
                crossNwUpdateInfoBus.updateWay = "NetworkStateService";
                crossNwUpdateInfoBus.nwsType = networkType;
                EventBus.getDefault().post(crossNwUpdateInfoBus);
            } else {
                Timber.d("[NetworkStateService][O]___");
            }
        }
    };

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //注册网络状态的广播,绑定到 mReceiver
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销接收
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        // 获取网络连接管理器
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 获取当前网络状态信息
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }
}
