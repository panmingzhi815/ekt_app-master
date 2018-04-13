package com.hbtl.ui.account.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.models.CommonAccountInfo;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

//import eu.siacs.eu.siacs.eu.siacs.eu.siac.eu.siacs.conversations.conversations.R;

public class CommonChangeInfoActivity extends BaseActivity implements OnClickListener {
    private String TAG = CommonChangeInfoActivity.class.getSimpleName();

    private CommonAccountInfo mAccount;

    private Activity mActivity;
    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;
    @BindView(R.id.left_button) Button mCancelButton;
    @BindView(R.id.right_button) Button mChangePasswordButton;
    @BindView(R.id.current_password) EditText mCurrentPassword;
    @BindView(R.id.new_password) EditText mNewPassword;
    @BindView(R.id.new_password_confirm) EditText mNewPasswordConfirm;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_change_password_activity);

        ButterKnife.bind(this);
        mActivity = CommonChangeInfoActivity.this;
        mAccount = CoamApplicationLoader.getInstance().appAccountInfo;

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        appNav_ToolBar.setTitle("常用联系人Tab-..");

        setSupportActionBar(appNav_ToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        Button mCancelButton = (Button) findViewById(R.id.left_button);
//        mCancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        this.mChangePasswordButton = (Button) findViewById(R.id.right_button);
//        this.mChangePasswordButton.setOnClickListener(this.mOnChangePasswordButtonClicked);
//        this.mCurrentPassword = (EditText) findViewById(R.id.current_password);
//        this.mNewPassword = (EditText) findViewById(R.id.new_password);
//        this.mNewPasswordConfirm = (EditText) findViewById(R.id.new_password_confirm);

        // 分别绑定点击事件
        mChangePasswordButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

//        this.mTheme = findTheme();
//        setTheme(this.mTheme);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ButterKnife.unbind(this);
    }

    //    @Override
//    public void onPasswordChangeSucceeded() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(CommonChangeInfoActivity.this, R.string.password_changed, Toast.LENGTH_LONG).show();
//                finish();
//            }
//        });
//    }

//    @Override
//    public void onPasswordChangeFailed() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mNewPassword.setError(getString(R.string.could_not_change_password));
//                mChangePasswordButton.setEnabled(true);
//                mChangePasswordButton.setTextColor(getPrimaryTextColor());
//                mChangePasswordButton.setText(R.string.change_password);
//            }
//        });
//
//    }

//    public void refreshUiReal() {
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_button: // 充值
                finish();
                break;
            case R.id.right_button: // 充值
                final String currentPassword = mCurrentPassword.getText().toString();
                final String newPassword = mNewPassword.getText().toString();
                final String newPasswordConfirm = mNewPasswordConfirm.getText().toString();
                if (!currentPassword.equals(mAccount.password)) {
                    mCurrentPassword.requestFocus();
                    mCurrentPassword.setError("getString(R.string.account_status_unauthorized)");
                } else if (!newPassword.equals(newPasswordConfirm)) {
                    mNewPasswordConfirm.requestFocus();
                    mNewPasswordConfirm.setError("getString(R.string.passwords_do_not_match)");
                } else if (newPassword.trim().isEmpty()) {
                    mNewPassword.requestFocus();
                    mNewPassword.setError("getString(R.string.password_should_not_be_empty)");
                } else {
                    mCurrentPassword.setError(null);
                    mNewPassword.setError(null);
                    mNewPasswordConfirm.setError(null);
                    ///xmppConnectionService.updateAccountPasswordOnServer(mAccount, newPassword, CommonChangeInfoActivity.this);
                    mChangePasswordButton.setEnabled(false);
                    ///mChangePasswordButton.setTextColor(getSecondaryTextColor());
                    mChangePasswordButton.setText("R.string.updating");
                }
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
        // TODO ...
    }
}
