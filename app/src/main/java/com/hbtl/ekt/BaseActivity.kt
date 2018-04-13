package com.hbtl.ekt

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.nfc.tech.NfcB
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.hbtl.app.CoamApplicationLoader
import com.hbtl.beans.CrossNwUpdateInfoBus
import com.hbtl.beans.HttpResourceLockedBus
import com.hbtl.models.CommonAccountInfo
import com.hbtl.service.NetUtils
import com.hbtl.service.NetworkType
import com.hbtl.utils.CommonUtils
import com.hbtl.utils.StatusBarUtil
import com.hbtl.utils.UiUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    // Android 状态栏工具类(实现沉浸式状态栏/变色状态栏)
    // http://laobie.github.io/android/2016/03/27/statusbar-util.html
    protected open fun setStatusBar() {
        //StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        val coi: Int
        if (Build.VERSION.SDK_INT >= 23) {
            coi = ContextCompat.getColor(this, R.color.main_toolbar_color);
        } else {
            coi = getResources().getColor(R.color.main_toolbar_color);
        }
        StatusBarUtil.setColor(this, coi, 120)
    }

    var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        mActivity = this

        // 设置状态来透明
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //getWindow().setStatusBarColor(Color.TRANSPARENT);

        //DebugUtils.d("BaseActivity savedInstanceState is null:" + (savedInstanceState == null));
        //DebugUtils.d("BaseActivity onCreate");
        if (savedInstanceState != null) {
            //            CoamApplicationLoader.getInstance().setCurrentUser((AppCoamUserInfo) savedInstanceState.getSerializable("currentUser"));
            CoamApplicationLoader.getInstance().appAccountInfo = savedInstanceState.getParcelable<Parcelable>("appCommonAccountInfo") as CommonAccountInfo
            //ShareSDK.initSDK(this)
        }

        // 参考 薄荷Toolbar(ActionBar)的适配方案 http://www.stormzhang.com/android/2015/08/16/boohee-toolbar/
        // 这句很关键,注意是调用父类的方法
        //        super.setContentView(R.layout.activity_base);
        // 经测试在代码里直接声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            var localLayoutParams: WindowManager.LayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags;
        }

        CoamApplicationLoader.getInstance().appDeviceInfo.screenHeight = UiUtils.getScreenHeight(this)
        CoamApplicationLoader.getInstance().appDeviceInfo.screenWidth = UiUtils.getScreenWidth(this)
        CoamApplicationLoader.getInstance().appDeviceInfo.statusBarHeight = UiUtils.getStatusBarHeight(this)
        CoamApplicationLoader.getInstance().appDeviceInfo.actionBarSize = UiUtils.getActionBarSize(this)

        // init 获取系统 NFC 模块...
        nfcAdapter = NfcAdapter.getDefaultAdapter(applicationContext)
        if (nfcAdapter == null) {
            Timber.e("设备不支持NFC！")
        } else if (!nfcAdapter!!.isEnabled()) {
            Timber.e("请在系统设置中先启用NFC功能！")
        } else {
            Timber.i("开启系统NFC功能...")
            initNfcInfo()
        }
//        if (nfcAdapter == null) Timber.d(NFC_LOG_TAG + "mAdapter is null");
//        else Timber.d(NFC_LOG_TAG + "mAdapter is not null");
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setStatusBar()
    }

    // 重写屏幕旋转的方法 参考 http://www.sunnyu.com/?p=223
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横向
            //            setContentView(R.layout.file_list_landscape);
            CoamApplicationLoader.getInstance().appDeviceInfo.screenHeight = UiUtils.getScreenHeight(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.screenWidth = UiUtils.getScreenWidth(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.statusBarHeight = UiUtils.getStatusBarHeight(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.actionBarSize = UiUtils.getActionBarSize(this)
        } else {
            //竖向
            //            setContentView(R.layout.file_list);
            CoamApplicationLoader.getInstance().appDeviceInfo.screenHeight = UiUtils.getScreenHeight(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.screenWidth = UiUtils.getScreenWidth(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.statusBarHeight = UiUtils.getStatusBarHeight(this)
            CoamApplicationLoader.getInstance().appDeviceInfo.actionBarSize = UiUtils.getActionBarSize(this)
        }
        //        Timber.i("SSS-CommonAccountSpaceActivity.java->getScreenHeight:" + UiUtils.getScreenHeight(this) + "-getStatusBarHeight:" + UiUtils.getStatusBarHeight(this) + "-getActionBarSize:" + UiUtils.getActionBarSize(this));
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState)
        //DebugUtils.d("BaseActivity onSaveInstanceState");
        outState.putParcelable("appCommonAccountInfo", CoamApplicationLoader.getInstance().appAccountInfo)
    }

    // nfc 模块标签...
    val NFC_LOG_TAG = "[NFC-LOG-TAG]"

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()

        Timber.d(NFC_LOG_TAG + "come into onResume 1")
        if (nfcAdapter != null) {
            startNfcListener()
        }
        Timber.d(NFC_LOG_TAG + "come into onResume 2")

        Timber.d(NFC_LOG_TAG + "pass onNewIntent 1.111111 action=" + intent.action)
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
//		mAdapter.disableForegroundDispatch(this);
        if (nfcAdapter != null) {
            stopNfcListener()
        }
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Subscribe
    fun onEventMainThread(httpResourceLockedBus: HttpResourceLockedBus) {
        Timber.i("III[httpResourceLockedBus.renderState: " + httpResourceLockedBus.renderState)
        //HttpUtils().showVerifyAuthDialog(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(crossNwUpdateInfoBus: CrossNwUpdateInfoBus?) {
        if (crossNwUpdateInfoBus == null) return

        val updateWay = crossNwUpdateInfoBus.updateWay
        val nwsType: NetworkType = crossNwUpdateInfoBus.nwsType

        // 检测网络...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mActivity?.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                val nwType = NetUtils(CoamApplicationLoader.appContextInstance).NET_WORK_TYPE
                Timber.d("[NetworkStateService][nwType: $nwType]")
            }
        }

        // 获取当前 Activity 对象名称
        var mnActivity: String = CommonUtils.getTopActivity(mActivity)
        when (mnActivity) {
            "com.hbtl.ui.menu.activity.MainMenuActivity" -> {

                // 调用子类回调...
                onNwUpdateEvent(nwsType);
            }
            "com.hbtl.ui.common.activity.CommonWelcomeSplashActivity" -> {

                // 调用子类回调...
                onNwUpdateEvent(nwsType);
            }
            else -> {
                Timber.i("[mnActivity: " + mnActivity + "]")
            }
        }
    }

    // 配置 NFC Listener...
    //private int mode = 2;//1,OTG; 2, NFC; //3, Bluetooth;
    private var nfcAdapter: NfcAdapter? = null
    private var nfcPendingIntent: PendingIntent? = null
    //滤掉组件无法响应和处理的Intent
    private var nfcTagDetected: IntentFilter? = null
    private var nfcTechLists: Array<Array<String>>? = null

    private fun initNfcInfo() {
        nfcPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        //		nfcTagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        nfcTagDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        nfcTagDetected?.addCategory(Intent.CATEGORY_DEFAULT)
        nfcTechLists = arrayOf(arrayOf(NfcB::class.java.name))
    }

    private fun startNfcListener() {
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, arrayOf<IntentFilter>(nfcTagDetected!!), nfcTechLists)
    }

    private fun stopNfcListener() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    // NFC 刷卡通知...
    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // 调用子类方法...
        inNewIntent(intent)
    }

    // NFC,事件通知消息...
    protected abstract fun inNewIntent(intent: Intent)

    // 网络变更,事件通知消息...
    protected abstract fun onNwUpdateEvent(networkType: NetworkType)

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //@JvmField
    //var outerFragmentView: FrameLayout? = null
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
