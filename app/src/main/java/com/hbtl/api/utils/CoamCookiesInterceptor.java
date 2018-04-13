package com.hbtl.api.utils;

/**
 * Created by yzhang on 2017/3/6.
 */

// Original written by tsuharesu
// Adapted to create a "drop it in and watch it work" approach by Nikhil Jha.
// Just add your package statement and drop it in the folder with all your other classes.

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences may ary, but this will work 99% of the time.
 * [AddCookiesInterceptor.java](https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b)
 */
public class CoamCookiesInterceptor implements Interceptor {
    private final static String T_Cookie_TAG = "AAAAAA|TCTCTC";
    private final static String Cross_Root_Domain = "coam.co";
    // We're storing our stuff in a database made just for cookies called PREF_COOKIES.
    // I reccomend you do this, and don't change this default value.
    private Context mContext;

    public CoamCookiesInterceptor(Context context) {
        this.mContext = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {

        Request originalRequest = chain.request();
        HttpUrl oRequestUrl = originalRequest.url();
        Object oRequestTag = originalRequest.tag();
        String rHost = oRequestUrl.host();
        // [Get root domain from request](http://stackoverflow.com/questions/17241532/get-root-domain-from-request)
        String crossRootDomain = rHost.replaceAll(".*\\.(?=.*\\.)", "");
        Timber.i(String.format(T_Cookie_TAG + "|[Start]-Sending request [tag:%s]on %s%n%s", oRequestTag, rHost, crossRootDomain));


        // 获取并添加Cookie至请求头...
        Request.Builder builder = chain.request().newBuilder();

        HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(mContext).getStringSet(crossRootDomain, new HashSet<String>());

        // Use the following if you need everything in one line.
        // Some APIs die if you do it differently.
        /*String cookiestring = "";
        for (String cookie : preferences) {
            String[] parser = cookie.split(";");
            cookiestring = cookiestring + parser[0] + "; ";
        }
        builder.addHeader("Cookie", cookiestring);
        */

        for (String cookie : preferences) {
            Timber.i(T_Cookie_TAG + "|[Loop]:cookie:" + cookie);
            builder.addHeader("Cookie", cookie);
        }
//        return chain.proceed(builder.build());

        // 获取并从响应头保存Cookie...
        Response originalResponse = chain.proceed(builder.build());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(mContext).getStringSet(crossRootDomain, new HashSet<String>());

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            memes.putStringSet(crossRootDomain, cookies).apply();
            memes.commit();
        }

        return originalResponse;
    }
}