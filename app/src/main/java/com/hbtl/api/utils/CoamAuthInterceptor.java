package com.hbtl.api.utils;

import android.content.Context;
import android.widget.Toast;

import com.hbtl.api.CoamHttpService;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.beans.HttpResourceLockedBus;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Connection;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by 亚飞 on 10/16/2015.
 * https://github.com/square/okhttp/wiki/Interceptors
 * http://stackoverflow.com/questions/28536522/intercept-and-retry-call-by-means-of-okhttp-interceptors
 * https://bng86.gitbooks.io/android-third-party-/content/okhttp.html
 * http://stackoverflow.com/questions/32196424/how-to-add-headers-to-okhttp-request-interceptor
 */
public class CoamAuthInterceptor implements Interceptor {

    public final static String T_Auth_TAG = "AAAAAA#TATATA";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        //第一次一般是还未被赋值,若有值则将sessionId发给服务器
        if (CoamHttpService.header_auth_token != null) {
            originalRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + CoamHttpService.header_auth_token)
                    .build();
        }

        // ekt token...
        String token = CoamApplicationLoader.getInstance().appRunInfo.token;
        if (token != null) {
            originalRequest = originalRequest.newBuilder()
                    .addHeader("x-access-token", "" + token)
                    .build();
        }

        long t1 = System.nanoTime();
        Connection oRequestConnection = chain.connection();
        HttpUrl oRequestUrl = originalRequest.url();
        Object oRequestTag = originalRequest.tag();
        Headers oRequestHeaders = originalRequest.headers();
        Timber.i(String.format(T_Auth_TAG + "|[Start]-Sending request [tag:%s]on %s%n%s", oRequestTag, oRequestConnection, oRequestHeaders));

        Response originalResponse = chain.proceed(originalRequest);

//        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
//            HashSet<String> cookies = new HashSet<>();
//
//            for (String header : originalResponse.headers("Set-Cookie")) {
//                cookies.add(header);
//            }
//        }

        final Context context = CoamApplicationLoader.appContextInstance;

        //            Token freshToken = mProvider.invalidateAppTokenAndGetNew();
//            String freshBearerToken = freshToken.getAccessToken();

        Timber.i(T_Auth_TAG + "|[Switch:Start](OkHttp.HTTP_STATE)[" + originalRequest.url() + "]...");
        // http://developer.android.com/reference/java/net/HttpURLConnection.html
        switch (originalResponse.code()) {
            case HttpURLConnection.HTTP_OK: //#200
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_OK + ")]");
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED: //#401
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_UNAUTHORIZED + ")]Authorization:" + oRequestHeaders.get("Authorization"));
//                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_UNAUTHORIZED + ")]CoamHttpService.token:" + CoamHttpService.token);

                //解决 401 错误
                // 首先尝试获取当前保存的用户登录账户密码信息...
                String basic_credential = null;
                String authName = "coam";
                String authPass = "";
                // 如果保存有用户账号密码则获取 Basic 认证信息...
                if (!authName.equals("") && !authPass.equals("")) {
                    //第一个参数为用户名,第二个参数为密码 - Base64 编码
                    basic_credential = Credentials.basic(authName, authPass);
                }

                // 1.首先尝试获取长效 token...
                String header_authorization = null;
                if (originalRequest.header("Authorization") == null) {
                    // 如果请求头不含有 Authorization:Token
                    // 默认使用用户账号密码而不使用临时 Token
                    // 如果保存有用户账号密码-则尝试发起一次 Basic 认证
                    if (basic_credential != null) header_authorization = basic_credential;
                } else {
                    // 如果请求头含有 Authorization:Token
                    // 以下检查保证本地有值 [CoamHttpService.header_auth_token != null]-[CoamHttpService.header_auth_tt != null]
                    // 如果已发起过 Token 请、求,则根据 Authorization 认证类型查看是否需要更新
                    String oRequestAuthorization = originalRequest.header("Authorization");
                    String[] oRequestAuthorizationInfo = oRequestAuthorization.split(" ");
                    String oRequestAuthWay = oRequestAuthorizationInfo[0];
                    String oRequestAuthInfo = oRequestAuthorizationInfo[1];
                    // 判断当前请求认证返回 401 的 Authorization 是否可续...
                    switch (oRequestAuthWay) {
                        case "Basic":
                            // 如果保存有用户账号密码-并且与上一次认证账户密码不同-则尝试再发起一次新的 Basic 认证...
                            if (!basic_credential.equals(oRequestAuthInfo))
                                header_authorization = basic_credential;
                            break;
                        case "Bearer":
                            // 判断当前请求的 Authorization 是否与本地最新的一致,如果本地已更新到新的授权 Token 则尝试直接用本地的 Authorization 发起当前请求
                            // 避免在 requestToken 到来后才处理到之前的 401 错误请求(有的请求慢于后续 requestToken 到来后重新发起 requestToken 请求的问题)
                            if (!CoamHttpService.header_auth_token.equals(oRequestAuthInfo)) {
                                header_authorization = "Bearer " + CoamHttpService.header_auth_token;
                            } else if (CoamHttpService.header_auth_tt.equals("verify")) {
                                // 如果长效token超过2小时 看是否可以以当前旧的长效 Token 续签到新的长效Token
                                CoamHttpService.coamRequestToken("refresh_token");
                                // 如果没有刷新到新的 Token,则清除本地Token,防止死循环
                                if (CoamHttpService.header_auth_token.equals(oRequestAuthInfo)) {
                                    CoamHttpService.saveCoamTokenInfo(null, null);
                                } else {
                                    header_authorization = "Bearer " + CoamHttpService.header_auth_token;
                                }
                            }
                            break;
                        default:
                            Timber.i(T_Auth_TAG + "|Oops:unkown oRequestAuthWay:" + oRequestAuthWay);
                            break;
                    }
                }

                // 2.如果没有 Authorization: Token 则尝试请求临时 token
                if (header_authorization == null) {
                    CoamHttpService.coamRequestToken("create_token");
                    if (CoamHttpService.header_auth_token != null)
                        header_authorization = "Bearer " + CoamHttpService.header_auth_token;
                }

                // 更新请求 token 后替换 Authorization 并重新发起请求
                if (header_authorization != null) {
                    Request newRequest = chain.request();
                    newRequest = newRequest.newBuilder()
                            //更新请求 Token
                            .addHeader("Authorization", header_authorization)
                            .build();

                    //添加请求Token后再次尝试请求 -> retry...
                    originalResponse = chain.proceed(newRequest);
                    Timber.i(T_Auth_TAG + "|CoamAuthInterceptor 401 Start NewRequest Successful ...");
                } else {
                    Timber.i(T_Auth_TAG + "|CoamAuthInterceptor 401 Start NewRequest Failed ...");
                }

                break;
            case HttpURLConnection.HTTP_FORBIDDEN: //#403
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_FORBIDDEN + ")]");
                Toast.makeText(context, "资源禁止访问", Toast.LENGTH_SHORT).show();
                break;
            case HttpURLConnection.HTTP_NOT_FOUND: //#404
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_NOT_FOUND + ")]");
                Toast.makeText(context, "您访问的资源不存在", Toast.LENGTH_SHORT).show();
                break;
            case HttpURLConnection.HTTP_BAD_METHOD: //#405
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_BAD_METHOD + ")]");
                break;
            case HttpURLConnection.HTTP_CLIENT_TIMEOUT: //#408
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_CLIENT_TIMEOUT + ")]");
                Toast.makeText(context, "请求超时,请稍后尝试", Toast.LENGTH_SHORT).show();
                break;
            case 423: //#423
                Timber.i(T_Auth_TAG + "|[Switch:case(423)]");

                // 传递到主 Activity 进行进一步处理
                HttpResourceLockedBus httpResourceLockedBus = new HttpResourceLockedBus();
                httpResourceLockedBus.renderState = "model.re_data.spaceInfo";
                //推送 EventBus 消息
                EventBus.getDefault().post(httpResourceLockedBus);

                break;
            case 424: //#424
                Timber.i(T_Auth_TAG + "|[Switch:case(424)]");
                String statusReason = originalResponse.message();
                //String statusReason = HUConnectionClient.getResponseMessage();
                //DebugUtils.d("statusReason:" + statusReason);
                switch (statusReason) {
                    case "CompanyUnAuthRefused": //未完成企业认证
//                        HttpUtils.gotoAuthActivity(context, "提醒", "您还没有完成企业认证, 确定现在去认证吗?", CompanyCertifyAuthActivity.class);
                        break;
                    case "TruckUnAuthRefused": //未完成货车认证
//                        HttpUtils.gotoAuthActivity(context, "提醒", "您还没有完成货车认证, 确定现在去认证吗?", TruckInfoAuthActivity.class);
                        break;
                    default:
                        break;
                }
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR: //#500
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_INTERNAL_ERROR + ")]");
                Toast.makeText(context, "服务器内部错误,请稍后尝试", Toast.LENGTH_SHORT).show();
                break;
            case HttpURLConnection.HTTP_BAD_GATEWAY: //#502
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_BAD_GATEWAY + ")]");
                Toast.makeText(context, "服务器繁忙", Toast.LENGTH_SHORT).show();
                break;
            case HttpURLConnection.HTTP_UNAVAILABLE: //#503
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_UNAVAILABLE + ")]");
                Toast.makeText(context, "您的请求过于频繁,请稍后尝试", Toast.LENGTH_SHORT).show();
                break;
            case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: //#504
                Timber.i(T_Auth_TAG + "|[Switch:case(" + HttpURLConnection.HTTP_GATEWAY_TIMEOUT + ")]");
                Toast.makeText(context, "您的网关超时,请检查网络连接", Toast.LENGTH_SHORT).show();
                break;
            default:
                Timber.i(T_Auth_TAG + "|[Switch:case(default)]");
                break;
        }
        Timber.i(T_Auth_TAG + "|[Switch:End](OkHttp.HTTP_STATE)...");

//        if (!originalResponse.isSuccessful()) {
//
//        } else if (originalResponse.isRedirect()) {
////            onResponseRedirect(mCtx, call, response);
//        } else {
////            onResponseError(mCtx, call, response);
//        }

        // update new access_token from response header
        Headers responseHeaders = originalResponse.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            // Timber.i("|Received response headers: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        // 如果返回有 Authorization:Token
        if (originalResponse.header("Authorization") != null) {
            String oResponseAuthorization = originalResponse.header("Authorization");
            String[] oResponseAuthorizationInfo = oResponseAuthorization.split(" ");
            String oResponseAuthWay = oResponseAuthorizationInfo[0];
            String oResponseAuthInfo = oResponseAuthorizationInfo[1];
            switch (oResponseAuthWay) {
                case "Basic":
                    break;
                case "Bearer":
                    // 更新本地 token 以便下次请求
                    CoamHttpService.saveCoamTokenInfo("verify", oResponseAuthInfo);
                    break;
                default:
                    break;
            }
        }

        // 调试的时候会报错误 [Android开发之Okhttp:java.lang.IllegalStateException: closed](http://www.cnblogs.com/liyiran/p/5498417.html)
        //String responseBody = originalResponse.body().string();
        // [java.lang.IllegalStateException: closed when trying to access response in onResponse(Response response)](https://github.com/square/okhttp/issues/1240)
        ResponseBody responseBodyCopy = originalResponse.peekBody(Long.MAX_VALUE);
        String responseBody = responseBodyCopy.string();
        Timber.i("WWWWWWW#0000000000000000000000000#originalResponse.request().url():" + originalResponse.request().url());
        Timber.i("WWWWWWW#0000000000000000000000000#[responseBody: " + responseBody + "]");
        try {
            JSONObject Jobject = new JSONObject(responseBody);
            //JSONArray Jarray = Jobject.getJSONArray("employees");
            int status = Jobject.getInt("status");
            //String message = Jobject.getString("message");
            switch (status) {
                case -198:// token过期
                case -199:// token错误
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long t2 = System.nanoTime();
        Headers oResponseHeaders = originalResponse.headers();
        Timber.i(String.format(T_Auth_TAG + "|[End]-Received response for [tag:%s] in %.1fms%n%s", oRequestTag, (t2 - t1) / 1e6d, oResponseHeaders));

        return originalResponse;
    }
}
