package com.hbtl.ui.account.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hbtl.service.SmsContentObserver;
import com.hbtl.ekt.R;
import com.hbtl.view.ToastHelper;
import com.hbtl.view.ToastHelper.ToastType;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountPasswordFragment extends Fragment implements OnClickListener {

    @BindView(R.id.aInfoPhone_MET) MaterialEditText aInfoPhone_MET;
    @BindView(R.id.smsSecurityCode_MET) MaterialEditText smsSecurityCode_MET;
    //    @BindView(R.id.sendSecurityCode_Btn) Button sendSecurityCode_Btn;
    @BindView(R.id.newAccountPassword_MET) MaterialEditText newAccountPassword_MET;
    //    @BindView(R.id.submitForm_Btn) Button submitForm_Btn;
    //    private EditText phoneNumET, smsSecurityCodeET, newPwdET;
    //    private ButtonRectangle submit, getCode;
//    private Button submit, getCode;
    private String phoneNum, smsSecurityCode, newPwd;
    private Handler mHandler;
    private Timer mTimer;
    private int time;
    private SmsContentObserver smsContentObserver;

    private String openAsId;//打开空间的用户 asId
    private String openAsType;//打开空间的用户 asType
    private boolean ifAccountSpace = false;//设定是否是当前登录用户的账户空间

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.account_password_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            openAsId = getArguments().getString("openAsId");
        }

        initHandler();
        initView(view);
    }

    private void initView(View view) {
//        sendSecurityCode_Btn.setOnClickListener(this);
//        submitForm_Btn.setOnClickListener(this);
        smsContentObserver = new SmsContentObserver(getActivity(), mHandler);
    }

    @SuppressLint("HandlerLeak")
    private void initHandler() {
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
//                    case 0:
//                        sendSecurityCode_Btn.setText(time + "s后重新获取");
//                        if (time <= 0) {
//                            mTimer.cancel();
//                            sendSecurityCode_Btn.setText("获取验证码");
//                            sendSecurityCode_Btn.setEnabled(true);
//                        }
//                        time--;
//                        break;
                    case 1:
                        ToastHelper.makeText(getActivity(), "修改成功", ToastHelper.LENGTH_LONG, ToastType.SUCCESS).show();
                        smsSecurityCode_MET.setText("");
                        newAccountPassword_MET.setText("");
                        break;
                    case 2:
                        ToastHelper.makeText(getActivity(), "修改失败", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                    case 3:
                        break;
                    case SmsContentObserver.RECEIVER_IDENTIFY_CODE:
                        smsSecurityCode_MET.setText((String) msg.obj);
                        break;
                    default:
                        break;
                }
            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSmsContentObservers();
    }

    private void registerSmsContentObservers() {
        Uri smsUri = Uri.parse("content://sms");
        //smsContentObserver是SmsContentObserver的一个实例,可以在onCreate中初始化
        getActivity().getContentResolver().registerContentObserver(smsUri, true, smsContentObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterSmsContentObservers();
    }

    private void unRegisterSmsContentObservers() {
        getActivity().getContentResolver().unregisterContentObserver(smsContentObserver);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
//            case R.id.sendSecurityCode_Btn:
//                phoneNum = aInfoPhone_MET.getText().toString().trim();
//                if (CoamTextUtils.isEmpty(phoneNum)) {
//                    ToastHelper.makeText(getActivity(), "请输入手机号", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                if (phoneNum.length() < 11) {
//                    ToastHelper.makeText(getActivity(), "手机号长度小于11位", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
////                sendSecurityCode_Btn.setEnabled(false);
//                CommonUtils.getSecurityCode(getActivity(), "AccountPasswordModify", phoneNum);
//                time = 10;
//                TimerTask mTimerTask = new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        mHandler.sendEmptyMessage(0);
//                    }
//                };
//                mTimer = new Timer();
//                mTimer.schedule(mTimerTask, 0, 1000);
//                break;

//            case R.id.submitForm_Btn:
//                phoneNum = aInfoPhone_MET.getText().toString().trim();
//                if (CoamTextUtils.isEmpty(phoneNum)) {
//                    ToastHelper.makeText(getActivity(), "请输入手机号", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                if (phoneNum.length() < 11) {
//                    ToastHelper.makeText(getActivity(), "手机号长度小于11位", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                smsSecurityCode = smsSecurityCode_MET.getText().toString().trim();
//                if (CoamTextUtils.isEmpty(smsSecurityCode)) {
//                    ToastHelper.makeText(getActivity(), "请输入验证码", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                newPwd = newAccountPassword_MET.getText().toString().trim();
//                if (CoamTextUtils.isEmpty(newPwd)) {
//                    ToastHelper.makeText(getActivity(), "请输入新密码", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                modifyPwd();
//
//                break;
            default:
                break;
        }
    }

//    private void modifyPwd() {
//
//        final Map<String, String> sParamList = new HashMap<String, String>();
//        sParamList.put("sendWay", "NewAccountPassword");
//        sParamList.put("verifyPhone", phoneNum);
//        sParamList.put("phoneSMSVerifyCode", smsSecurityCode);
//        sParamList.put("newAccountPassword", newPwd);
//
//        CoamHttpService.getInstance().sendRetrievePassword(sParamList)
//                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CaiSendRetrievePasswordModel>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
//                        e.printStackTrace();
//
//                        ToastHelper.makeText(getActivity(), "修改失败", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    }
//
//                    @Override
//                    public void onNext(CoamBaseResponse<CoamResponseModel.CaiSendRetrievePasswordModel> model) {
//                        if (model.re_info.result) {
//                            ToastHelper.makeText(getActivity(), "修改成功", ToastHelper.LENGTH_SHORT, ToastType.SUCCESS).show();
//                            smsSecurityCode_MET.setText("");
//                            newAccountPassword_MET.setText("");
//                        } else {
//                            ToastHelper.makeText(getActivity(), "修改失败", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                        }
//                    }
//                });
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }
}
