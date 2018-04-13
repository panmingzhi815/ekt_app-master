package com.hbtl.ui.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.common.activity.CommonWelcomeSplashActivity;
import com.hbtl.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppSettingAboutActivity extends BaseActivity implements OnClickListener {

    @BindView(R.id.appOfficialWebInfo_TV) TextView appOfficialWebInfo_TV;
    @BindView(R.id.appServiceTermInfo_TV) TextView appServiceTermInfo_TV;

    @BindView(R.id.appUpdate_ProBar) ProgressBar appUpdate_ProBar;

    @BindView(R.id.appVersionLayout_RL) RelativeLayout appVersionLayout_RL;
    @BindView(R.id.appVersion_TV) TextView appVersion_TV;

    @BindView(R.id.appFunctionLayout_RL) RelativeLayout appFunctionLayout_RL;
    @BindView(R.id.appFeedbackLayout_RL) RelativeLayout appFeedbackLayout_RL;
    @BindView(R.id.contactUsLayout_RL) RelativeLayout contactUsLayout_RL;

    private Activity mActivity;

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_setting_about_activity);
        ButterKnife.bind(this);

        //initUpdateHandler();
        mActivity = AppSettingAboutActivity.this;

        // Inflate a menu to be displayed in the toolbar
        //appNav_ToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_account_space));
        appNav_ToolBar.setTitle("关于我们");

        setSupportActionBar(appNav_ToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appVersion_TV.setText(CommonUtils.getVersionName(this));
        appVersionLayout_RL.setOnClickListener(this);
        appFunctionLayout_RL.setOnClickListener(this);
        appFeedbackLayout_RL.setOnClickListener(this);
        contactUsLayout_RL.setOnClickListener(this);

        appOfficialWebInfo_TV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        appServiceTermInfo_TV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        appOfficialWebInfo_TV.setOnClickListener(this);
        appServiceTermInfo_TV.setOnClickListener(this);

        //注册事件监听器
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.appOfficialWebInfo_TV:
                CommonUtils.gotoWebActivity(mActivity, "腾旅官网", getResources().getString(R.string.official_web));
                break;
            case R.id.appServiceTermInfo_TV:
                CommonUtils.gotoWebActivity(mActivity, "服务条款", getResources().getString(R.string.service_term));
                break;
            case R.id.appVersionLayout_RL:
                appUpdate_ProBar.setVisibility(View.VISIBLE);
                appVersion_TV.setVisibility(View.GONE);
                //检查更新,统一转至CoamUtils下
                new CommonUtils().checkAppUpdate(mActivity);
                break;
            case R.id.appFunctionLayout_RL:
                Intent intent = new Intent(this, CommonWelcomeSplashActivity.class);
                intent.putExtra("manual", true);
                startActivity(intent);
                break;
            case R.id.appFeedbackLayout_RL:
                //startActivity(new Intent(AppSettingAboutActivity.this, ChatActivity.class).putExtra("userId", "M-1"));
                break;
            case R.id.contactUsLayout_RL:
                startActivity(new Intent(AppSettingAboutActivity.this, AppSettingContactUsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册事件监听器
        EventBus.getDefault().unregister(this);
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
