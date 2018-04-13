package com.hbtl.ui.common.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hbtl.ekt.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeFragment extends Fragment {

    //    private WebView mWebView;
    @BindView(R.id.welcomeWeb_WV) WebView welcomeWeb_WV;

    private String url;

    public static WelcomeFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        welcomeFragment.setArguments(bundle);
        return welcomeFragment;
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);

        url = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.app_welcome_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
//        mWebView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSettings = welcomeWeb_WV.getSettings();
        webSettings = welcomeWeb_WV.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);

        // 加载本地资源文件
        // Android-WebView中远端链接使用本地js文件 http://www.jianshu.com/p/5eda46ca8e37
        webSettings.setJavaScriptEnabled(true);

        welcomeWeb_WV.setWebViewClient(new WebViewClient() {

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
            }

        });

        welcomeWeb_WV.loadUrl(url);
    }
}
