package com.hbtl.ui.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hbtl.api.CoamHttpService;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.ekt.BaseActivity;
import com.hbtl.ekt.R;
import com.hbtl.service.NetworkType;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class CommonWebViewActivity extends BaseActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public final static String T_WebView_TAG = "WWWWWW";

    @BindView(R.id.appNav_ToolBar) Toolbar appNav_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_web_view_activity);
        ButterKnife.bind(this);

        // Inflate a menu to be displayed in the toolbar
        //appNav_ToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.my_account_space));
        appNav_ToolBar.setTitle(getIntent().getStringExtra("title"));

        setSupportActionBar(appNav_ToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示左侧回退按钮

        appNav_ToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ///mProgressBar = (ProgressBar) findViewById(R.id.appLoading_ProBar);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                ///mProgressBar.setVisibility(View.GONE);
            }

        });

        // 同步 okHttpClient Cookies
        Context context = CoamApplicationLoader.appContextInstance;
        syncCookie(context, CoamHttpService.MPI_SERVER);

        // 加载网页
        mWebView.loadUrl(getIntent().getStringExtra("website"));
    }

    // 同步 Cookie 到 WebView...
    public void syncCookie(Context context, String url) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除
            cookieManager.removeAllCookie();
            String oldCookie = cookieManager.getCookie(url);
            Timber.d(T_WebView_TAG + "|oldCookie:" + oldCookie);

            URL aURL = new URL(url);

            StringBuilder sbCookie = new StringBuilder();
            sbCookie.append(String.format("PHPSESSID" + "=%s", CoamHttpService.getOkHttpClientCookie(CoamHttpService.MPI_HOST, "PHPSESSID")));
            //webview在使用cookie前会前判断保存cookie的domain和当前要请求的domain是否相同,相同才会发送cookie
            sbCookie.append(String.format(";domain=%s", aURL.getHost())); //注意,是getHost(),不是getAuthority(),
            sbCookie.append(String.format(";path=%s", "/"));

            String cookieValue = sbCookie.toString();
            cookieManager.setCookie(url, cookieValue);
            CookieSyncManager.getInstance().sync();

            String newCookie = cookieManager.getCookie(url);
            Timber.d(T_WebView_TAG + "|newCookie:" + newCookie);
        } catch (Exception e) {
            e.printStackTrace();
            Timber.d(T_WebView_TAG + "|Error:" + e.getMessage());
        }
    }

    // 应在退出时移除 Cookie
    // [[Android] Webview利用CookieSyncManager获取或设置Cookies的策略](http://blog.csdn.net/stzy00/article/details/50586979)
    public void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();

        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync();
        } else {
            CookieManager.getInstance().flush();
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
