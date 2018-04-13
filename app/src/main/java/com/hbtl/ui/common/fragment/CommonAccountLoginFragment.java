package com.hbtl.ui.common.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hbtl.api.CoamHttpService;
import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.models.CommonAccountInfo;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.ui.common.activity.RetrievePasswordActivity;
import com.hbtl.ui.menu.activity.MainMenuActivity;
import com.hbtl.utils.CommonUtils;
//import com.coam.utils.SNSUtils;
import com.hbtl.view.ToastHelper;
import com.hbtl.view.ToastHelper.ToastType;
import com.hbtl.ekt.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.internal.http2.Http2;
import timber.log.Timber;

//import com.coam.service.LocationService;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.PlatformDb;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.sina.weibo.SinaWeibo;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.wechat.friends.Wechat;

//import okhttp3.internal.framed.Http2;

public class CommonAccountLoginFragment extends Fragment implements OnClickListener {

    // 登陆输入表单
    @BindView(R.id.loginAccountName_MET)
    EditText loginAccountName_MET;
    @BindView(R.id.loginPassword_MET)
    EditText loginPassword_MET;
    @BindView(R.id.submitLoginForm_Btn)
    AppCompatButton submitLoginForm_Btn;
    @BindView(R.id.retrievePassword_TV)
    TextView retrievePassword_TV;

    // 第三方登录相关
    @BindView(R.id.qqLogin_IV)
    View qqLogin_IV;
    @BindView(R.id.weChatLogin_IV)
    View weChatLogin_IV;
    @BindView(R.id.weiBoLogin_IV)
    View weiBoLogin_IV;
    @BindView(R.id.snsLoginDivider_LL)
    View snsLoginDivider_LL;
    @BindView(R.id.snsLoginLayout_LL)
    View snsLoginLayout_LL;

    private String account;
    private String password;
    //    private Handler loginHandler;
    private SharedPreferences sharedPreferences;

    private Handler bindHandler;
    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR = 3;
    private static final int MSG_AUTH_COMPLETE = 4;
    //    private static final int CHECK_REGISTER_FAILED = 5;
//    private static final int CHECK_HAS_REGISTER = 6;
//    private static final int CHECK_NOT_REGISTER = 7;
    private Activity mActivity;
    private String snsId, snsWay;
    private int actionType;
    private String targetSnsWay;
    private String targetSnsId;

    public static CommonAccountLoginFragment newInstance(int actionType, String snsId, String snsWay) {
        Bundle bundle = new Bundle();
        bundle.putInt("actionType", actionType);
        bundle.putString("snsId", snsId);
        bundle.putString("snsWay", snsWay);
        CommonAccountLoginFragment loginFragment = new CommonAccountLoginFragment();
        loginFragment.setArguments(bundle);
        return loginFragment;
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        this.mActivity = getActivity();

        actionType = getArguments().getInt("actionType");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        //注册:三个参数分别是,消息订阅者(接收者),接收方法名,事件类
        //EventBus.getDefault().register(getActivity(), "setTextA", Networker.class);
    }

    /**
     * 与注册对应的方法名和参数,没有后缀,默认使用PostThread模式,即跟发送事件在同一线程执行
     *
     * @param event
     */
//    public void setTextA(Networker event) {
//        //textView.setText("A");
//        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW...");
//    }

    //事件1接收者:在主线程接收
    @Subscribe
    public void onEvent(String event) {
        //mShowInfo1.setText(event);
        Timber.i("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE..1.");
    }

    //事件2接收者:在主线程接收自定义MsgBean消息
    //@Subscribe
//    public void onEvent(MsgBean event){
//        //mShowInfo21.setText(event.getMsg());
//        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE..2.");
//    }
    //事件3接收者:在主线程接收
    @Subscribe
    public void onEventMainThread(Integer event) {
        //mShowInfo2.setText(event ));
        Timber.i("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE..2.");
    }

    @Subscribe
    public void onEventMainThread(String event) {
        //mShowInfo2.setText(event ));
        Timber.i("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE..3.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.common_account_login_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    public boolean validate() {
        boolean valid = true;

        String email = loginAccountName_MET.getText().toString();
        String password = loginPassword_MET.getText().toString();

//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        if (email.isEmpty() || email.length() < 2 || email.length() > 10) {
            loginAccountName_MET.setError("enter a valid email address");
            ToastHelper.makeText(mActivity, "请输入账号!", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
            valid = false;
        } else {
            loginAccountName_MET.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            loginPassword_MET.setError("between 4 and 10 alphanumeric characters");
            ToastHelper.makeText(mActivity, "请输入密码!", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
            valid = false;
        } else {
            loginPassword_MET.setError(null);
        }

        return valid;
    }

//    public void onLoginSuccess() {
//        submitLoginForm_Btn.setEnabled(true);
//        //finish();
//    }
//
//    public void onLoginFailed() {
//        Toast.makeText(getActivity().getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
//
//        submitLoginForm_Btn.setEnabled(true);
//    }

    private void initView(View view) {
        ///initLoginHandler();
        initBindHandler();
        sharedPreferences = mActivity.getSharedPreferences(CoamBuildVars.SP_CONFIG, Context.MODE_PRIVATE);
//        common_account_login_fragment = (ButtonRectangle) view.findViewById(R.id.common_account_login_fragment);
//        common_account_login_fragment.setRippleSpeed(80);
//        submitLoginForm_Btn = (Button) view.findViewById(R.id.login);
        //common_account_login_fragment.setTextColor(Color.WHITE);
//        retrievePassword_TV = (TextView) view.findViewById(R.id.findPwd);
//        loginAccountName_MET = (EditText) view.findViewById(R.id.account);
//        loginPassword_MET = (EditText) view.findViewById(R.id.password);

        submitLoginForm_Btn.setOnClickListener(this);
        retrievePassword_TV.setOnClickListener(this);

//        ShareSDK.initSDK(mActivity);
//        qqLogin_IV = view.findViewById(R.id.qqLogin);
//        weChatLogin_IV = view.findViewById(R.id.wechatLogin);
//        weiBoLogin_IV = view.findViewById(R.id.sinaWeiboLogin);
        qqLogin_IV.setOnClickListener(this);
        weChatLogin_IV.setOnClickListener(this);
        weiBoLogin_IV.setOnClickListener(this);
//        snsLoginDivider_LL = view.findViewById(R.id.snsDivider);
//        snsLoginLayout_LL = view.findViewById(R.id.snsLayout);
        String accountName = "", password = "";
        if (CoamApplicationLoader.appContextInstance.appAccountInfo.accountName != null)
            accountName = CoamApplicationLoader.appContextInstance.appAccountInfo.accountName;
        if (CoamApplicationLoader.appContextInstance.appAccountInfo.password != null)
            password = CoamApplicationLoader.appContextInstance.appAccountInfo.password;
        loginAccountName_MET.setText(accountName);
        loginPassword_MET.setText(password);
//            loginAccountName_MET.setText(sharedPreferences.getString(CoamBuildVars.SY_ACCOUNT, null));
//            loginPassword_MET.setText(sharedPreferences.getString(CoamBuildVars.SY_PASSWORD, null));
        ///common_account_login_fragment.setText("登陆");
        snsLoginLayout_LL.setVisibility(View.VISIBLE);
        snsLoginDivider_LL.setVisibility(View.VISIBLE);
    }

    private ProgressDialog progressDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitLoginForm_Btn:

                if (!validate()) {
//                    onLoginFailed();
                    submitLoginForm_Btn.setEnabled(true);
                    return;
                }

                ///submitLoginForm_Btn.setEnabled(false);
                progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("正在登陆...");
                ///progressDialog.show();


                account = loginAccountName_MET.getText().toString().trim();
                password = loginPassword_MET.getText().toString().trim();
//                if (TextUtils.isEmpty(account)) {
//                    ToastHelper.makeText(activity, "请输入账号!", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    ToastHelper.makeText(activity, "请输入密码!", ToastHelper.LENGTH_SHORT, ToastType.ERROR).show();
//                    return;
//                }

                // Should be done by a DI
//                RestApi mRestApi = new RestApi();
//                //mRestApi.listRepositories("saulmm");
//                mRestApi.listGitFeeds("coam");
                //v.setOnClickListener(v1 -> Toast.makeText(getActivity(), "clicked", Toast.LENGTH_LONG).show());
                // mLaunchRxButton.setOnClickListener(v -> startActivity(new Intent(this, RxMainActivity.class)));
                Timber.i("CommonAccountLoginFragment Start Fetch User Details...");
                //new GithubServiceManager().fetchUserDetails();

                //发起Token请求验证测试...
//                System.out.println("Start Request Context...");
//                new Thread(()->{
//                    HttpUtils.getToken(getContext());
//                }).start();
//                System.out.println("End Request Context...");

                //暂时禁用处理登陆点击事件
//                CoamUtils.setPhpSession(getActivity(), null);
                login("test", account, password);
//                sleep(20);
//                login("2222", account, password);
//                sleep(20);
//                login("3333", account, password);
//                sleep(20);
//                login("4444", account, password);
//                sleep(20);

                break;
            case R.id.qqLogin_IV:
//                SNSUtils.bindSNS(QQ.NAME, this);
                break;
            case R.id.weChatLogin_IV:
//                SNSUtils.bindSNS(Wechat.NAME, this);
                break;
            case R.id.weiBoLogin_IV:
//                SNSUtils.bindSNS(SinaWeibo.NAME, this);
                break;
            case R.id.retrievePassword_TV:
                startActivity(new Intent(mActivity, RetrievePasswordActivity.class));
                break;
        }
    }

    private static final Logger frameLogger = frameLogger();

    public static Logger frameLogger() {
        Logger frameLogger = Logger.getLogger(Http2.class.getName() + "$FrameLogger");
        frameLogger.setLevel(Level.FINE);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s%n", record.getMessage());
            }
        });
        frameLogger.addHandler(handler);
        return frameLogger;
    }

    private void login(final String loginWay, final String accountName, final String password) {
        //单独构建 OKHttp请求参数
        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("login_way", "customer");
        sParamList.put("login_name", accountName);
        sParamList.put("password", password);
        sParamList.put("appBtcId", "coam-ios-(null)-682");
        sParamList.put("appBtpId", "coam/u/(null)");
        sParamList.put("appVersion", CommonUtils.getVersionName(getActivity()));
        sParamList.put("runPlatform", "android-" + android.os.Build.VERSION.RELEASE);

        CoamHttpService.getInstance().sendLoginAccount(loginWay, sParamList)
                .subscribe(new Observer<CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                        // handle completed
                        Timber.i("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT-onCompleted...");
                        //重新启用登陆按钮点击事件
                        //submitLoginForm_Btn.setOnClickListener(CommonAccountLoginFragment.this);

                        progressDialog.dismiss();
                        submitLoginForm_Btn.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("#|@|EEEEEEEE[E-M]:" + e.getMessage());
                        e.printStackTrace();

                        //可能有异常
                        ToastHelper.makeText(mActivity, "登录失败_Z", ToastHelper.LENGTH_LONG, ToastType.ERROR).show();

                        progressDialog.dismiss();
                        submitLoginForm_Btn.setEnabled(true);
                    }

                    @Override
                    public void onNext(CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel> model) {
                        //CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel> re_model = (CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel>) model.re_data;
                        if (model.re_info.result) {
                            CommonAccountInfo loginAccountInfo = model.re_data.loginAccountInfo;

                            // 更新覆盖掉从服务器返回的 md5 加密密码
                            loginAccountInfo.password = password;
                            loginAccountInfo.ifLogin = true;// 更新账户为已登陆状态
                            CoamApplicationLoader.getInstance().appAccountInfo = loginAccountInfo;
                            CoamApplicationLoader.getInstance().syncAccountPreferences("save");

                            gotoMenuActivity();
                        } else {
                            String msg_obj = model.re_info.re_desc;
                            ToastHelper.makeText(mActivity, (String) msg_obj, ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        }
                    }
                });
    }

    private void gotoMenuActivity() {
        Intent intent = new Intent(mActivity, MainMenuActivity.class);
        startActivity(intent);

        mActivity.finish();
    }

    @SuppressLint("HandlerLeak")
    private void initBindHandler() {
        bindHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_AUTH_CANCEL:
                        //取消授权
                        ToastHelper.makeText(mActivity, R.string.auth_cancel, ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                    case MSG_AUTH_ERROR:
                        //授权失败
                        ToastHelper.makeText(mActivity, R.string.auth_error, ToastHelper.LENGTH_LONG, ToastType.ERROR).show();
                        break;
                    case MSG_AUTH_COMPLETE:
                        //授权成功
                        ToastHelper.makeText(mActivity, R.string.auth_complete, ToastHelper.LENGTH_LONG, ToastType.SUCCESS).show();
                        Object[] objs = (Object[]) msg.obj;
                        String platName = (String) objs[0];
                        String unionId = null;
                        break;
                }
            }

        };
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
