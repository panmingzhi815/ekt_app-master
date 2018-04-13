package com.hbtl.ui.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hbtl.api.CoamHttpService;
import com.hbtl.api.model.CoamResponseInner;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.BuildConfig;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.utils.FileUtils;
import com.hbtl.utils.ImageUtils;
import com.hbtl.view.ToastHelper;
import com.joanzapata.iconify.widget.IconTextView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ScenicAuthActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ScenicAuthActivity.class.getSimpleName();
    private Activity mActivity;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    @BindView(R.id.scenicAuthResult_LL) LinearLayout scenicAuthResult_LL;

    @BindView(R.id.scenicAuthResultHeader_TV) TextView scenicAuthResultHeader_TV;
    @BindView(R.id.scenicAuthResultInfo_TV) TextView scenicAuthResultInfo_TV;

    @BindView(R.id.authAccountAvatar_IV) ImageView authAccountAvatar_IV;

    @BindView(R.id.scenicAuthBody_LL) LinearLayout scenicAuthBody_LL;
    @BindView(R.id.scenicAuthInfo_LL) LinearLayout scenicAuthInfo_LL;
    @BindView(R.id.authAccountNameInfo_TV) TextView authAccountNameInfo_TV;
    @BindView(R.id.authAccountGenderInfo_TV) TextView authAccountGenderInfo_TV;
    @BindView(R.id.authAccountIdNoInfo_TV) TextView authAccountIdNoInfo_TV;

    @BindView(R.id.scenicAuthFooter_LL) LinearLayout scenicAuthFooter_LL;
    @BindView(R.id.scenicAuthIcon_ITV) IconTextView scenicAuthIcon_ITV;
    @BindView(R.id.scenicAuthInfo_TV) TextView scenicAuthInfo_TV;

    private String authWay;// 认证方式 [QrCode: 二维码验证| IdCard: 身份证验证]
    private int authCode;// 认证状态码
    private String authResult;// 认证结果
    private String authFacePath;// 人脸识别本地验证...
    private boolean retainFace;// 人脸识别本地验证...
    private CoamResponseInner.MemberInfo memberInfo;// 校验用户帐号信息

    // [Android自带TTS与科大讯飞语音SDK](http://fanzhenyu.me/2016/08/08/Android%E8%87%AA%E5%B8%A6TTS%E4%B8%8E%E7%A7%91%E5%A4%A7%E8%AE%AF%E9%A3%9E%E8%AF%AD%E9%9F%B3SDK/)
    // [Android TTS 中文 文字转语音 使用TextToSpeech Svox](https://www.pocketdigi.com/20110720/410.html)
    private TextToSpeech mTts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.app_scenic_auth_actitity);
        mActivity = ScenicAuthActivity.this;
        ButterKnife.bind(this);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        appNav_ToolBar.setTitle("授权验证结果");

        setSupportActionBar(appNav_ToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 注册点击事件
        scenicAuthFooter_LL.setOnClickListener(this);

        // 获取传递过来的参数...
        authWay = getIntent().getExtras().getString("authWay");
        authCode = getIntent().getExtras().getInt("authCode");
        authResult = getIntent().getExtras().getString("authResult");
        authFacePath = getIntent().getExtras().getString("authFacePath");
        retainFace = getIntent().getExtras().getBoolean("retainFace");
        memberInfo = (CoamResponseInner.MemberInfo) getIntent().getParcelableExtra("memberInfo");

        // 语音合成...
        initTts();

        // 渲染视图
        initView();
    }

    public void initView() {
        Bitmap bitmap = ImageUtils.getSmallBitmap(authFacePath);
        authAccountAvatar_IV.setImageBitmap(bitmap);

        // 删除本地缓存图片文件...
        if (!retainFace) FileUtils.deleteFile(mActivity, authFacePath);

        // 根据返回状态码确定授权认证结果...
        switch (authCode) {
            case CoamHttpService.Code_OK_EnterByFace:// 二维码入园验证成功
                scenicAuthResultHeader_TV.setText("验证结果: 验证通过,请入园!");
                scenicAuthIcon_ITV.setText("{fa-modx spin}");
                scenicAuthInfo_TV.setText("请放行");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.main_color_nor));
                break;
            case CoamHttpService.Code_OK_EnterByQrCode:// 二维码入园验证成功
                scenicAuthResultHeader_TV.setText("验证结果: 验证通过,请入园!");
                scenicAuthIcon_ITV.setText("{fa-modx spin}");
                scenicAuthInfo_TV.setText("请放行");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.main_color_nor));
                break;
            case CoamHttpService.Code_OK_EnterByIdCard:// 身份证入园验证成功
                scenicAuthResultHeader_TV.setText("验证结果: 验证通过,请入园!");
                scenicAuthIcon_ITV.setText("{fa-modx spin}");
                scenicAuthInfo_TV.setText("请放行");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.main_color_nor));
                break;
            case CoamHttpService.Code_FaceMismatch:// 人脸不匹配
                scenicAuthResultHeader_TV.setText("验证结果: 验证失败,信息匹配失败!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            case CoamHttpService.Code_OutOfEnterCountOneTourToday:// 当天入园次数受限
                scenicAuthResultHeader_TV.setText("验证结果: [天限]禁止入园!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            case CoamHttpService.Code_OutOfEnterCountOneTourYear:// 当年入园次数受限
                scenicAuthResultHeader_TV.setText("验证结果: [年限]禁止入园!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            case CoamHttpService.Code_QrCodeError:// 二维码已超时
                scenicAuthResultHeader_TV.setText("验证结果: 验证失败,信息匹配失败!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            case CoamHttpService.Code_MemberInfoNotFound:// 未找到会员信息
                scenicAuthResultHeader_TV.setText("验证结果: 验证失败,无此会员!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.gray));
                break;
            case CoamHttpService._Code_OK_EnterByFace:// 二维码入园验证成功(离线)
                scenicAuthResultHeader_TV.setText("验证结果：验证通过请入园！");
                scenicAuthIcon_ITV.setText("{fa-modx spin}");
                scenicAuthInfo_TV.setText("请放行");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.main_color_nor));
                break;
            case CoamHttpService._Code_QrCodeError:// 二维码入园验证失败(在线)
                scenicAuthResultHeader_TV.setText("验证结果: 在线验证失败,请重试!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            case CoamHttpService._Code_OK_WxApp_EnterByIdCard:// 当前网络不稳定,验证失败
                scenicAuthResultHeader_TV.setText("验证结果: 验证通过!");
                scenicAuthIcon_ITV.setText("{fa-modx spin}");
                scenicAuthInfo_TV.setText("请放行");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.main_color_nor));
                break;
            case CoamHttpService._Code_OutOfEnterCountOneTourToday:// 当天入园次数受限(离线)
                scenicAuthResultHeader_TV.setText("验证结果: [天限]禁止入园!");
                scenicAuthIcon_ITV.setText("{fa-object-group}");
                scenicAuthInfo_TV.setText("重新验证");
                scenicAuthResult_LL.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.theme_danger));
                break;
            default:
                // TODO...
                //ToastHelper.makeText(mActivity, "抱歉,未知的认证验证状态码[authCode: " + authCode + "]...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                break;
        }
        String text = "[" + authResult + "]";
        if (BuildConfig.DEBUG) text = "[调试]_[" + authCode + ": " + authResult + "]";
        scenicAuthResultInfo_TV.setText(text);

        if (Arrays.asList(new Integer[]{CoamHttpService.Code_OK_EnterByQrCode, CoamHttpService.Code_OK_EnterByIdCard, CoamHttpService.Code_FaceMismatch, CoamHttpService.Code_OutOfEnterCountOneTourToday, CoamHttpService.Code_OutOfEnterCountOneTourYear}).contains(authCode)) {
            // 显示用户账户信息...
            authAccountNameInfo_TV.setText(memberInfo.name);
            authAccountGenderInfo_TV.setText("M".equals(memberInfo.sex) ? "男" : "女");
            authAccountIdNoInfo_TV.setText(memberInfo.idcard);
        } else if (Arrays.asList(new Integer[]{CoamHttpService.Code_QrCodeError, CoamHttpService.Code_MemberInfoNotFound, CoamHttpService._Code_QrCodeError, CoamHttpService._Code_OK_EnterByFace, CoamHttpService._Code_OutOfEnterCountOneTourToday, CoamHttpService._Code_OK_WxApp_EnterByIdCard}).contains(authCode)) {
            scenicAuthInfo_LL.setVisibility(View.GONE);
        }
        // 如果返回的空用户信息(则隐藏用户信息)...
        if (memberInfo == null) {
            scenicAuthInfo_LL.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 语音合成...
                mTts.speak(authResult, TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 600);
    }

    private void initTts() {
        TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
                if (status == TextToSpeech.SUCCESS) {
                    // Set preferred language to US english.
                    // Note that a language may not be available, and the result will indicate this.
                    int result = mTts.setLanguage(Locale.CHINESE);
                    // Try this someday for some interesting results.
                    // int result mTts.setLanguage(Locale.FRANCE);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Lanuage data is missing or the language is not supported.
                        Timber.e("[###]Language is not available.");
                    } else {
                        // Check the documentation for other possible result codes.
                        // For example, the language may be available for the locale,
                        // but not for the specified country and variant.

                        // The TTS engine has been successfully initialized.
                        // Allow the user to press the button for the app to speak again.
                        //mAgainButton.setEnabled(true);
                        Timber.e("[###]Language is available.");
                    }
                } else {
                    // Initialization failed.
                    Timber.e("[###]Could not initialize TextToSpeech.");
                }
            }
        };

        // 初始化 TTS 语音识别服务 ...
        mTts = new TextToSpeech(this, mInitListener);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scenicAuthFooter_LL: // 通用二维码扫码

                if (BuildConfig.DEBUG)
                    ToastHelper.makeText(mActivity, "[调试]_捕获[scenicAuthFooter_LL]点击事件...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();

                // 结束授权结果页面...
                finish();

                // Drop all pending entries in the playback queue.
                //mTts.speak("你好", TextToSpeech.QUEUE_FLUSH, null);
                break;
            default:
                Timber.i("iwwwwwwwwwwwwwwwwwwwwwwwww---");
                break;
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
