package com.hbtl.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.hbtl.config.CoamBuildVars;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.common.fragment.CommonAccountLoginFragment;
import com.hbtl.utils.CommonUtils;
import com.hbtl.view.ToolbarTabsView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginWithRegisterActivity extends BaseActivity implements OnClickListener {
    private Fragment leftTabFragment;
    private Fragment rightTabFragment;
    private Fragment showTabFragment;

    @BindView(R.id.appNav_ToolBar) ToolbarTabsView appNav_ToolBar;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login_with_register_activity);
        ButterKnife.bind(this);

        mContext = this;

        // 注册 EventBus 事件监听器
        EventBus.getDefault().register(this);

        ///initUpdateHandler();
        initView();


        //检查更新,统一转至CoamUtils下
        //checkUpdate();
        //new CommonUtils().checkAppUpdate();
    }

    private void initView() {

        // Inflate a menu to be displayed in the toolbar
//        appNav_ToolBar.inflateMenu(R.menu.share_info_list_menu);
        // Set an OnMenuItemClickListener to handle menu item clicks
//        appNav_ToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                // Handle the menu item
//                return true;
//            }
//        });
        //appNav_ToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_account_space));
        appNav_ToolBar.setTitle("");

        setSupportActionBar(appNav_ToolBar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮
        //getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);

//        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//                //finish();
//            }
//        });

        // 设置 TabTag
        appNav_ToolBar.setCenterTag("登陆");
        //appNav_ToolBar.setLeftTag("登陆");
        //appNav_ToolBar.setRightTag("注册");

        // 绑定左中右切换Tab监听器
        appNav_ToolBar.setSwitchTabListen(new ToolbarTabsView.OnTabSwitchListener() {
            @Override
            public void onLeftTabSwitch() {
                //changeFragment(leftTabFragment);
            }

            @Override
            public void onCenterTabSwitch() {

            }

            @Override
            public void onRightTabSwitch() {
                //changeFragment(rightTabFragment);
            }
        });

        leftTabFragment = CommonAccountLoginFragment.newInstance(1, null, null);
        //rightTabFragment = CommonAccountRegisterFragment.newInstance(CoamBuildVars.TYPE_LOGIN, null, null);
        showTabFragment = leftTabFragment;

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, showTabFragment).commit();

        appNav_ToolBar.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                SharedPreferences sharedPreferences = getSharedPreferences(CoamBuildVars.STATUSBAR_HEIGHT, Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt(CoamBuildVars.STATUSBAR_HEIGHT, CommonUtils.getStatusBarHeight(LoginWithRegisterActivity.this)).commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            default:
                break;
        }
    }

    private void changeFragment(Fragment fragment) {
        if (fragment != showTabFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!fragment.isAdded()) {
                transaction.hide(showTabFragment).add(R.id.fragmentContainer, fragment).commit();
            } else {
                transaction.hide(showTabFragment).show(fragment).commit();
            }
            showTabFragment = fragment;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // 取消已注册 EventBus 事件监听器
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
//        if (arg1 == CoamBuildVars.LOGIN_RESULT_CODE) {
//            finish();
//        }
    }

    //避免页面的重叠
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        //super.onSaveInstanceState(outState);
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
