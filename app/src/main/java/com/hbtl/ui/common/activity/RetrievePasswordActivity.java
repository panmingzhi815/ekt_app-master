package com.hbtl.ui.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;
import com.hbtl.ui.account.fragment.AccountPasswordFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RetrievePasswordActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_retrieve_password_activity);
        ButterKnife.bind(this);

        initView();
    }


    private void initView() {

        // Inflate a menu to be displayed in the toolbar
        //appNav_ToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_account_space));
        appNav_ToolBar.setTitle("找回密码");

        setSupportActionBar(appNav_ToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new AccountPasswordFragment()).commit();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

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
        // TODO ...
    }
}
