package com.hbtl.ui.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hbtl.api.CoamHttpService;
import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.CrossAppSettingBus;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.ekt.R;
import com.hbtl.models.CommonAppRunInfo;
import com.hbtl.service.NetworkType;
import com.hbtl.tm.AndroidUtilities;
import com.hbtl.tm.LocaleController;
import com.hbtl.tm.cells.InfoColorCell;
import com.hbtl.tm.cells.InfoHeaderCell;
import com.hbtl.tm.cells.InfoLabelCell;
import com.hbtl.tm.cells.InfoLocationCell;
import com.hbtl.tm.cells.InfoPrivacyCell;
import com.hbtl.tm.cells.InfoSnsCell;
import com.hbtl.tm.cells.InfoSwitchCell;
import com.hbtl.tm.cells.InfoTagCell;
import com.hbtl.tm.cells.InfoTextCell;
import com.hbtl.tm.components.LayoutHelper;
import com.hbtl.tm.messenger.browser.Browser;
import com.hbtl.utils.CommonUtils;
import com.hbtl.view.ColorPickerView;
import com.hbtl.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class AppSettingActivity extends BaseActivity implements View.OnClickListener {

    private Activity mActivity;

    // 是否授权认证...
    private boolean ifAuth = false;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    // 软件设置
    @BindView(R.id.asiApp_LL) LinearLayout asiApp_LL;
    @BindView(R.id.asiAppHeader_Ihc) InfoHeaderCell asiAppHeader_Ihc;
    @BindView(R.id.asiAppInfoLanguage_Itc) InfoTagCell asiAppInfoLanguage_Itc;
    @BindView(R.id.asiAppInfoAudio_Isc) InfoSwitchCell asiAppInfoAudio_Isc;
    @BindView(R.id.asiAppInfoOae_Isc) InfoSwitchCell asiAppInfoOae_Isc;
    @BindView(R.id.asiAppInfoPush_Isc) InfoSwitchCell asiAppInfoPush_Isc;

    // 测试环境
    @BindView(R.id.asiTest_LL) LinearLayout asiTest_LL;
    @BindView(R.id.asiTestHeader_Ihc) InfoHeaderCell asiTestHeader_Ihc;
    @BindView(R.id.asiTestInfoSip_Itc) InfoTextCell asiTestInfoSip_Itc;
    @BindView(R.id.asiTestInfoOud_Itc) InfoTagCell asiTestInfoOud_Itc;
    @BindView(R.id.asiTestInfoAuto_Isc) InfoSwitchCell asiTestInfoAuto_Isc;
    @BindView(R.id.asiTestInfoMan_Itc) InfoTextCell asiTestInfoMan_Itc;
    @BindView(R.id.asiTestInfoVoice_Itc) InfoTextCell asiTestInfoVoice_Itc;
    @BindView(R.id.asiTestInfoColor_Icc) InfoColorCell asiTestInfoColor_Icc;
    @BindView(R.id.asiTestInfoPrivacy_Ipc) InfoPrivacyCell asiTestInfoPrivacy_Ipc;
    @BindView(R.id.asiTestInfoSns_Isc) InfoSnsCell asiTestInfoSns_Isc;
    @BindView(R.id.asiTestInfoLocation_Ilc) InfoLocationCell asiTestInfoLocation_Ilc;

    // 关于腾旅
    @BindView(R.id.asiEkt_LL) LinearLayout asiEkt_LL;
    @BindView(R.id.asiEktHeader_Ihc) InfoHeaderCell asiEktHeader_Ihc;
    @BindView(R.id.asiEktInfoFaq_Itc) InfoTagCell asiEktInfoFaq_Itc;
    @BindView(R.id.asiEktInfoAbout_Itc) InfoTagCell asiEktInfoAbout_Itc;
    @BindView(R.id.asiEktFooter_Ilc) InfoLabelCell asiEktFooter_Ilc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_app_setting_activity);
        ButterKnife.bind(this);
        mActivity = AppSettingActivity.this;

        appNav_ToolBar.setTitle("软件服务设置");

        setSupportActionBar(appNav_ToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //注册事件监听器
        EventBus.getDefault().register(this);

        // 绑定点击事件,触发 touch ripple 效果...
        asiAppInfoLanguage_Itc.setOnClickListener(this);
        asiAppInfoAudio_Isc.setOnClickListener(this);
        asiAppInfoOae_Isc.setOnClickListener(this);
        asiAppInfoPush_Isc.setOnClickListener(this);

        // 如果是测试环境-则显示配置项...
        if (BuildConfig.DEBUG) {
            asiTest_LL.setVisibility(View.VISIBLE);
            asiTestInfoSip_Itc.setOnClickListener(this);
            asiTestInfoOud_Itc.setOnClickListener(this);
            asiTestInfoAuto_Isc.setOnClickListener(this);
            asiTestInfoMan_Itc.setOnClickListener(this);
            asiTestInfoVoice_Itc.setOnClickListener(this);
            asiTestInfoColor_Icc.setOnClickListener(this);
            asiTestInfoSns_Isc.setOnClickListener(this);
            asiTestInfoLocation_Ilc.setOnClickListener(this);

            asiTestInfoVoice_Itc.setMultilineDetail(false);

            asiTestInfoColor_Icc.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), 0xff00ff00, true);

            asiTestInfoPrivacy_Ipc.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", R.string.KeepMediaInfo)));
            asiTestInfoPrivacy_Ipc.setBackgroundResource(R.drawable.greydivider_bottom);
            //asiTestInfoPrivacy_Ipc.setBackgroundResource(R.drawable.greydivider);

            //asiTestInfoLocation_Ilc.setLocationInfo("mAccountInfo.aInfoLocation", 22, 55);
            //asiTestInfoLocation_Ilc.setMultilineDetail(true);// 开启多行显示模式,不自动截断
            //asiTestInfoLocation_Ilc.setTextAndValue("尚未确定坐标位置=尚未确定坐标位置=尚未确定坐标位置=尚未确定坐标位置=", "用户地址", true);


            //asiTestInfoSns_Isc.setBindInfo("WeChat", null);
            //asiTestInfoSns_Isc.setTextAndValue("尚未绑定", "微信", false);
        }

        asiEktInfoFaq_Itc.setOnClickListener(this);
        asiEktInfoAbout_Itc.setOnClickListener(this);
        asiEktFooter_Ilc.setOnClickListener(this);

        // 设置测试服务器
        String mpiServer = CoamApplicationLoader.getInstance().appRunInfo.mpiServer;
        asiTestInfoSip_Itc.setLabelText(getResources().getString(R.string.TestServerHost), mpiServer, true);

        // 设置离线数据上报量...
        int oUpDn = CoamApplicationLoader.getInstance().appRunInfo.oUpDn;
        asiTestInfoOud_Itc.setLabelTag("离线数据单次上报量", String.valueOf(oUpDn), true);
        //asiTestInfoOud_Itc.setTextAndIcon("离线数据单次上报量", R.drawable.icon_check, true);

        // 底部程序版本信息-TextInfoCell
        String CPU_ABI = android.os.Build.CPU_ABI;
        int versionCode = CommonUtils.getVersionCode(mActivity);
        String versionName = CommonUtils.getVersionName(mActivity);
        asiEktFooter_Ilc.setInfo(String.format(Locale.US, "Ekt for Android [v%s] (%d) [%s]", versionName, versionCode, CPU_ABI), false);
    }

    // 更改服务器
    public void showEnvSipEditView(String content) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.TestServerHost)
                //.content(content)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(10, 50)
                .positiveText(R.string.TestServerMpi)
                .negativeText("取消")
                .input(
                        //R.string.TestServerMpiInputHint,
                        //R.string.input_hint,
                        "请输入服务器地址",
                        content,
                        false,
                        (dialog, input) -> {
                            String mpiServer = input.toString();
                            // 更新服务器接口地址提示
                            ToastHelper.makeText(mActivity, "更新服务器请求接口地址[mpiServer: " + mpiServer + "]", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO);

                            CrossAppSettingBus crossAppSettingBus = new CrossAppSettingBus();
                            crossAppSettingBus.appSetWay = "mpiServer";
                            crossAppSettingBus.mpiServer = mpiServer;
                            EventBus.getDefault().post(crossAppSettingBus);
                        })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        //DialogUtils.showProgressDialog(mActivity, "正在解除绑定...");
                        //String mpiServer = materialDialog.getInputEditText().getText().toString();
                        //showToast("---更新内容, " + mpiServer + "!");
                    }
                })
                .show();
    }

    // Custom View Dialog
    private EditText authPasswordInput;
    private View adminAuthAction;

    // 本地管理权限授权验证
    public void showAdminAuthView() {
        MaterialDialog dialog =
                new MaterialDialog.Builder(this)
                        .title(R.string.adminAuthHeader)
                        .customView(R.layout.app_admin_auth_dialog, true)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .inputRange(6, 12)
                        .positiveText(R.string.adminAuthSubmit)
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog1, which) -> {
                            String authPassword = authPasswordInput.getText().toString();
                            Timber.d("Password: " + authPassword);
                            // 验证密码是否正确
                            if (authPassword.equals(CoamBuildVars.EKT_ADMIN_AUTH_PASSWORD)) {
                                ifAuth = true;
                                ToastHelper.makeText(mActivity, "恭喜,授权验证通过,请再次更新配置环境...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.SUCCESS).show();

                                // 重新设置服务器配置...
                                //showEnvSipEditView();
                            } else {
                                ToastHelper.makeText(mActivity, "抱歉,密码错误,授权访问被拒绝...", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.ERROR).show();
                            }
                        })
                        .build();

        adminAuthAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions
        authPasswordInput = (EditText) dialog.getCustomView().findViewById(R.id.authPassword);
        authPasswordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adminAuthAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

        dialog.show();
        adminAuthAction.setEnabled(false); // disabled by default
    }

    // 登陆Http接口调用
    @Subscribe
    public void onEventMainThread(CrossAppSettingBus crossAppSettingBus) {
        //可能有异常
        if (crossAppSettingBus == null) {
            return;
        }

        if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_事件总线消息...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

        String appSetWay = crossAppSettingBus.appSetWay;
        String mpiServer = crossAppSettingBus.mpiServer;

        // 设置方式...
        switch (appSetWay) {
            case "mpiServer":
                final Map<String, String> sParamList = new HashMap<String, String>();
                String deviceId = CoamApplicationLoader.getInstance().appRunInfo.deviceId;
                long timestamp = System.currentTimeMillis();
                sParamList.put("deviceid", deviceId);
                sParamList.put("timestamp", String.valueOf(timestamp));

                CoamHttpService.getInstance(mpiServer).appGatePing(sParamList)
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

                                // 修改服务器地址网络错误
                                ToastHelper.makeText(mActivity, "服务器地址 Ping 失败,更新操作被拒绝...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                            }

                            @Override
                            public void onNext(CoamBaseResponse<CoamResponseModel.CsAppGatePingModel> model) {
                                if (model.re_code == 0) {
                                    // 修改服务器地址网络通过
                                    ToastHelper.makeText(mActivity, "服务器 Ping 通过,地址更新成功...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

                                    //CommonAppRunInfo commonAppRunInfo = CoamApplicationLoader.getInstance().appRunInfo;
                                    CoamApplicationLoader.getInstance().appRunInfo.mpiServer = mpiServer;
                                    CoamApplicationLoader.getInstance().syncRunPreferences("save");
                                    // 初始化网络请求实例...
                                    CoamHttpService.getInstance().initInstance();

                                    // 更新内容...
                                    asiTestInfoSip_Itc.setLabelText(getResources().getString(R.string.TestServerHost), mpiServer, true);
                                } else {
                                    // 服务器网络地址返回非预期数据问题
                                    ToastHelper.makeText(mActivity, "服务器 Ping 失败,[" + model.re_info.re_desc + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消注册事件监听器
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //@ 通用软件运行设置...
            case R.id.asiAppHeader_Ihc:
                // TODO: ...
                break;
            // 系统语言设置
            case R.id.asiAppInfoLanguage_Itc:
                ToastHelper.makeText(mActivity, "[抱歉,暂不支持设置系统语言]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.WARNING).show();

                // 设置语言...
                //if (BuildConfig.DEBUG) startActivity(new Intent(AppSettingActivity.this, AppLanguageSelectActivity.class));
                break;
            // 使用扬声器播放语音
            case R.id.asiAppInfoAudio_Isc:
                ToastHelper.makeText(mActivity, "暂不支持关闭[扬声器播放语音功能]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                break;
            // 离线数据
            case R.id.asiAppInfoOae_Isc:
                ToastHelper.makeText(mActivity, "暂不支持关闭[离线数据上报功能]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                break;
            // 消息推送服务
            case R.id.asiAppInfoPush_Isc:
                ToastHelper.makeText(mActivity, "暂不支持开启[消息推送服务]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                break;

            //@ 软件测试环境设置...
            case R.id.asiTestHeader_Ihc:
                break;
            // 服务器 IP
            case R.id.asiTestInfoSip_Itc:
                if (!ifAuth) {
                    showAdminAuthView();
                    return;
                }

                CommonAppRunInfo commonAppRunInfo = CoamApplicationLoader.getInstance().appRunInfo;
                String mpiServer = commonAppRunInfo.mpiServer;
                showEnvSipEditView(mpiServer);
                break;
            // 单次上报数据量
            case R.id.asiTestInfoOud_Itc:
                if (!ifAuth) {
                    showAdminAuthView();
                    return;
                }

                // 离线数据单次上报量
                int oUpDn = CoamApplicationLoader.getInstance().appRunInfo.oUpDn;

                // 显示选择框...
                AlertDialog.Builder ad_builder = new AlertDialog.Builder(mActivity);
                ad_builder.setTitle("更新离线数据单次上报量");
                final NumberPicker numberPicker = new NumberPicker(mActivity);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(10);
                numberPicker.setValue(oUpDn);
                ad_builder.setView(numberPicker);
                ad_builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 保存设置值...
                        int oUpDn = numberPicker.getValue();
                        CoamApplicationLoader.getInstance().appRunInfo.oUpDn = oUpDn;
                        CoamApplicationLoader.getInstance().syncRunPreferences("save");

                        // 设置离线数据上报量...
                        asiTestInfoOud_Itc.setLabelTag("离线数据单次上报量", String.valueOf(oUpDn), true);
                    }
                });
                ad_builder.create().show();
                break;
            case R.id.asiTestInfoAuto_Isc:
                asiTestInfoAuto_Isc.setChecked(false);
                break;
            // 微信绑定
            case R.id.asiTestInfoSns_Isc:
//                BottomSheet.Builder builder = new BottomSheet.Builder(mActivity);
//
//                CharSequence items[];
//                items = new CharSequence[]{LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache), LocaleController.getString("DeleteMegaMenu", R.string.DeleteMegaMenu)};
//                builder.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, final int which) {
//                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
//                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
//                        if (which == 0) {
//                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
//                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    ToastHelper.makeText(mActivity, "[Mask: AreYouSureClearHistoryChannel]", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();
//                                }
//                            });
//                        } else {
//                            builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
//                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    ToastHelper.makeText(mActivity, "[Mask: ChannelLeaveAlert]", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.INFO).show();
//                                }
//                            });
//                        }
//                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
//                        builder.create().show();
//                    }
//                });
//                builder.create().show();
                break;
            // LED 颜色设置
            case R.id.asiTestInfoColor_Icc:
                LinearLayout mLinearLayout = new LinearLayout(mActivity);
                mLinearLayout.setOrientation(LinearLayout.VERTICAL);
                final ColorPickerView colorPickerView = new ColorPickerView(mActivity);
                mLinearLayout.addView(colorPickerView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));

                SharedPreferences preferences = CoamApplicationLoader.appContextInstance.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
                colorPickerView.setOldCenterColor(preferences.getInt("MessagesLed", 0xff00ff00));

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mActivity);
                mBuilder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
                mBuilder.setView(mLinearLayout);
                mBuilder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        asiTestInfoColor_Icc.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), colorPickerView.getColor(), true);
                    }
                });
                mBuilder.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        asiTestInfoColor_Icc.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), 0, true);
                    }
                });
                mBuilder.create().show();
                break;
            // 铃声
            case R.id.asiTestInfoVoice_Itc:
                try {
                    SharedPreferences mPreferences = CoamApplicationLoader.appContextInstance.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
                    Intent tmpIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                    tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                    tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    Uri currentSound = null;

                    String defaultPath = null;
                    Uri defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }

                    String path = mPreferences.getString("GlobalSoundPath", defaultPath);
                    if (path != null && !path.equals("NoSound")) {
                        if (path.equals(defaultPath)) {
                            currentSound = defaultUri;
                        } else {
                            currentSound = Uri.parse(path);
                        }
                    }
                    tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentSound);
                    startActivityForResult(tmpIntent, 33333);
                } catch (Exception e) {
                    Timber.e("tmessages" + e);
                }
                break;
            // 漫游时
            case R.id.asiTestInfoMan_Itc:
//                final boolean maskValues[] = new boolean[6];
//                BottomSheet.Builder bs_builder = new BottomSheet.Builder(mActivity);
//
//                LinearLayout linearLayout = new LinearLayout(mActivity);
//                linearLayout.setOrientation(LinearLayout.VERTICAL);
//                for (int a = 0; a < 6; a++) {
//                    String name = "name" + a;
//                    InfoCbCell checkBoxCell = new InfoCbCell(mActivity);
//                    checkBoxCell.setTag(a);
//                    checkBoxCell.setBackgroundResource(R.drawable.telegram_list_selector);
//                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
//                    checkBoxCell.setText(name, "", maskValues[a], true);
//                    checkBoxCell.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            InfoCbCell cell = (InfoCbCell) v;
//                            int num = (Integer) cell.getTag();
//                            maskValues[num] = !maskValues[num];
//                            cell.setChecked(maskValues[num], true);
//                        }
//                    });
//                }
//                BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(mActivity, 2);
//                cell.setBackgroundResource(R.drawable.telegram_list_selector);
//                cell.setTextAndIcon("保存", R.drawable.qq_logo);
//                cell.setTextColor(0xffE91E63);
//                cell.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int newMask = 0;
//                        ToastHelper.makeText(mActivity, "[newMask: " + newMask + "]", ToastHelper.LENGTH_SHORT, ToastHelper.ToastType.WARNING).show();
//                    }
//                });
//                linearLayout.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 48));
//                bs_builder.setCustomView(linearLayout);
//                //showDialog(bs_builder.create());
//                bs_builder.create().show();
                break;

            //@ 软件服务商支持设置...
            case R.id.asiEktHeader_Ihc:
                break;
            // 常见问题 .
            case R.id.asiEktInfoFaq_Itc:
                ToastHelper.makeText(mActivity, "请联系腾旅人工客服[400-8006666]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                //Browser.openUrl(mActivity, LocaleController.getString("TelegramFaqUrl", R.string.TelegramFaqUrl));
                break;
            // 关于 ELT .
            case R.id.asiEktInfoAbout_Itc:
                Browser.openUrl(mActivity, LocaleController.getString("official_web", R.string.official_web));
                //CommonUtils.gotoWebActivity(mActivity, "腾旅官网", getResources().getString(R.string.official_web));
                break;
            case R.id.asiEktFooter_Ilc:
                // TODO: ...
                break;
            default:
                // TODO...
                Timber.i("iwwwwwwwwwwwwwwwwwwwwwwwww---");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String name = null;
            if (ringtone != null) {
                Ringtone rng = RingtoneManager.getRingtone(mActivity, ringtone);
                if (rng != null) {
                    if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                        name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                    } else {
                        name = rng.getTitle(mActivity);
                    }
                    rng.stop();
                }
            }

            if (BuildConfig.DEBUG) ToastHelper.makeText(mActivity, "[调试]_[name: " + name + "][ringtone: " + ringtone.toString() + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

            SharedPreferences preferences = CoamApplicationLoader.appContextInstance.getSharedPreferences("Notifications", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            if (name != null && ringtone != null) {
                editor.putString("GlobalSound", name);
                editor.putString("GlobalSoundPath", ringtone.toString());
            } else {
                editor.putString("GlobalSound", "NoSound");
                editor.putString("GlobalSoundPath", "NoSound");
            }
        }
    }

    @Override
    protected void inNewIntent(@NotNull Intent intent) {
        // TODO ...
    }

    @Override
    protected void onNwUpdateEvent(@NotNull NetworkType networkType) {
        // TODO ...
    }
}
