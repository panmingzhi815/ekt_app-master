package com.hbtl.api.utils;

/**
 * Created by yafei on 2/8/2016.
 * <p>
 * 自动管理 Cookies
 */

//import com.hbtl.api.session.PersistentCookieStore;
//import com.hbtl.app.CoamApplicationLoader;
//
//import java.util.List;
//
//import okhttp3.Cookie;
//import okhttp3.CookieJar;
//import okhttp3.HttpUrl;
//import timber.log.Timber;

/**
 * 自动管理 Cookies
 */
//public class CookiesManager implements CookieJar {
//    private final PersistentCookieStore cookieStore = new PersistentCookieStore(CoamApplicationLoader.appContextInstance);
//
//    @Override
//    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//        if (cookies != null && cookies.size() > 0) {
//            for (Cookie item : cookies) {
//                cookieStore.add(url, item);
//                Timber.i("CCCC:CookiesManager-saveFromResponse|url:" + url + "|item.name:" + item.name() + "|item.value:" + item.value() + "|item.toString:" + item.toString());
//            }
//        }
//    }
//
//    @Override
//    public List<Cookie> loadForRequest(HttpUrl url) {
//        List<Cookie> cookies = cookieStore.get(url);
//        for (Cookie item : cookies) {
//            Timber.i("CCCC:CookiesManager-loadForRequest|url:" + url + "|item.name:" + item.name() + "|item.value:" + item.value() + "|item.toString:" + item.toString());
//        }
//        return cookies;
//    }
//
//    public PersistentCookieStore getPersistentCookieStore() {
//        return cookieStore;
//    }
//}
