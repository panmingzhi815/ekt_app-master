package com.hbtl.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hbtl.api.model.CoamBaseResponse;
import com.hbtl.api.model.CoamResponseModel;
import com.hbtl.api.utils.CoamAuthInterceptor;
import com.hbtl.api.utils.CoamCookiesInterceptor;
import com.hbtl.app.CoamApplicationLoader;
import com.hbtl.config.CoamBuildVars;
import com.hbtl.utils.CommonUtils;
import com.hbtl.utils.RsaUtils;
import com.hbtl.view.ToastHelper;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import timber.log.BuildConfig;
import timber.log.Timber;

// [Retrofit入门使用教程](http://yaohepeng.com/2016/03/31/Retrofit%E5%85%A5%E9%97%A8%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B/)
// [RxJava入门使用教程](http://yaohepeng.com/2016/04/01/RxJava%E5%85%A5%E9%97%A8%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B/)
public class CoamHttpService<T> implements CoamApiInterface {

    public final static String T_Hts_TAG = "AAAAAA#HtsHtsHts";

//    public static final int PER_PAGE = 0;
//    public static final String APP_ENDPOINT = "https://mpi." + CoamHttpService.MAIN_TESTER_API_SERVER_DOMAIN;
//    public static final String WWW_ENDPOINT = "https://www." + CoamHttpService.MAIN_TESTER_API_SERVER_DOMAIN;

    private CoamApiInterface mCoamApi;
    public static String php_session_id = null;
    public static String header_auth_token = null;
    public static String header_auth_tt = null;//[test|tmp|verify]
    //public static String requestTokenState = "no";// no|pending

    public CoamHttpService(CoamApiInterface coamApi) {
        this.mCoamApi = coamApi;
    }

    // 更新本地设置的值...
//    public void updateHttpMpiServer() {
//        String mpiServer = CoamApplicationLoader.getInstance().appPreferences.
//        this.MPI_SERVER = mpiServer;
//    }

//    public static OkHttpClient client = new OkHttpClient().newBuilder()
//            .addInterceptor(new MyInterceptors())
//            .build();


//    //在访问HttpMethods时创建单例
//    private static class SingletonHolder {
//
//        static Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(APP_ENDPOINT)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .client(HttpUtils.okHttpClient) //默认使用 OkHttp 当做底层请求,配置自定义 拦截器 这里需要手动指定 OkHttpClient
//                .build();
//
//        // 获取并保存 sessionId ,需要研究跨域sessionid是否覆盖问题...
////        Map<String,List<String>> haha = new HashMap<>();
////        HttpUtils.okHttpClient.getCookieHandler().get(APP_ENDPOINT,haha);
//
////        HttpUtils.okHttpClient
////        List<HttpCookie> hCookies = ((CookieManager) HttpUtils.okHttpClient.getCookieHandler()).getCookieStore().getCookies();
////        Timber.i(hCookies.toArray().toString());
////
////        for (int i = 0; i < hCookies.size(); i++) {
////            //Timber.i("Y-" + hCookies.get(i).getName());
////            //这里是读取Cookie['PHPSESSID']的值存在静态变量中,保证每次都是同一个值
////            if ("PHPSESSID".equals(hCookies.get(i).getName())) {
////                CoamUtils.setPhpSession(CoamApplicationLoader.appContextInstance, hCookies.get(i).getValue());
////                break;
////                //Timber.i("Is an PHP_SESSION_ID...");
////            }
////        }
//
//        //mService = retrofit.create(mCoamApiInterface.class);
//        //return new CoamHttpService(retrofit.create(mCoamApiInterface.class));
//
//        private static final CoamHttpService INSTANCE = new CoamHttpService(retrofit.create(mCoamApiInterface.class));
//    }

    //默认使用 APP_ENDPOINT 请求数据
//    public static CoamHttpService getInstance() {
////        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl(APP_ENDPOINT)
////                .addConverterFactory(GsonConverterFactory.create())
////                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
////                .client(HttpUtils.okHttpClient) //默认使用 OkHttp 当做底层请求,配置自定义 拦截器 这里需要手动指定 OkHttpClient
////                .build();
////
////        // 获取并保存 sessionId ,需要研究跨域sessionid是否覆盖问题...
//////        Map<String,List<String>> haha = new HashMap<>();
//////        HttpUtils.okHttpClient.getCookieHandler().get(APP_ENDPOINT,haha);
////
//////        HttpUtils.okHttpClient
//////        List<HttpCookie> hCookies = ((CookieManager) HttpUtils.okHttpClient.getCookieHandler()).getCookieStore().getCookies();
//////        Timber.i(hCookies.toArray().toString());
//////
//////        for (int i = 0; i < hCookies.size(); i++) {
//////            //Timber.i("Y-" + hCookies.get(i).getName());
//////            //这里是读取Cookie['PHPSESSID']的值存在静态变量中,保证每次都是同一个值
//////            if ("PHPSESSID".equals(hCookies.get(i).getName())) {
//////                CoamUtils.setPhpSession(CoamApplicationLoader.appContextInstance, hCookies.get(i).getValue());
//////                break;
//////                //Timber.i("Is an PHP_SESSION_ID...");
//////            }
//////        }
////
////        //mService = retrofit.create(mCoamApiInterface.class);
////        return new CoamHttpService(retrofit.create(mCoamApiInterface.class));
//
//        return SingletonHolder.INSTANCE;
//    }


    // 参考单例设计模式
    private static volatile CoamHttpService Instance = null;  // <<< 这里添加了 volatile

    public static CoamHttpService getInstance() {
        CoamHttpService inst = Instance;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (CoamHttpService.class) {
                inst = Instance;
                if (inst == null) {
                    //inst = new CoamHttpService();

                    // 初始化加载 [header_auth_token|header_auth_tt]...
                    loadCoamTokenInfo();

                    // 使用临时配置环境...
                    String MPI_SERVER = CoamApplicationLoader.getInstance().appRunInfo.mpiServer;

                    //默认使用 APP_ENDPOINT 请求数据
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(MPI_SERVER)
                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(okHttpClient) //默认使用 OkHttp 当做底层请求,配置自定义 拦截器 这里需要手动指定 OkHttpClient
                            .build();

                    // 获取并保存 sessionId ,需要研究跨域sessionid是否覆盖问题...
//        Map<String,List<String>> haha = new HashMap<>();
//        HttpUtils.okHttpClient.getCookieHandler().get(APP_ENDPOINT,haha);

//        HttpUtils.okHttpClient
//        List<HttpCookie> hCookies = ((CookieManager) HttpUtils.okHttpClient.getCookieHandler()).getCookieStore().getCookies();
//        Timber.i(hCookies.toArray().toString());
//
//        for (int i = 0; i < hCookies.size(); i++) {
//            //Timber.i("Y-" + hCookies.get(i).getName());
//            //这里是读取Cookie['PHPSESSID']的值存在静态变量中,保证每次都是同一个值
//            if ("PHPSESSID".equals(hCookies.get(i).getName())) {
//                CoamUtils.setPhpSession(CoamApplicationLoader.appContextInstance, hCookies.get(i).getValue());
//                break;
//                //Timber.i("Is an PHP_SESSION_ID...");
//            }
//        }

                    //mService = retrofit.create(mCoamApiInterface.class);
                    //return new CoamHttpService(retrofit.create(mCoamApiInterface.class));

                    //private static final CoamHttpService INSTANCE = new CoamHttpService(retrofit.create(mCoamApiInterface.class));

                    inst = new CoamHttpService(retrofit.create(CoamApiInterface.class));
                    Instance = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    // 指定请求目标 WWW_ENDPOINT. -- 不使用单例模式...
    public static CoamHttpService getInstance(String appoint) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(appoint)
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient) //默认使用 OkHttp 当做底层请求,配置自定义 拦截器 这里需要手动指定 OkHttpClient
                .build();

        //mService = retrofit.create(mCoamApiInterface.class);
        return new CoamHttpService(retrofit.create(CoamApiInterface.class));
    }

    // 修改全局访问地址后初始化 HttpClient 实例...
    public void initInstance() {
        Instance = null;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     * @param <T>   Subscriber真正需要的数据类型,也就是Data部分的数据类型
     *  https://github.com/tough1985/RxjavaRetrofitDemo
     */
//    private class HttpResultFunc<T> implements Func1<CoamBaseResponse<T>, T> {
//
//        @Override
//        public T call(CoamBaseResponse<T> baseResponseModel) {
//            if (baseResponseModel.result == false) {
//                //throw new ApiException(100);
//                // 如果 {result: true}
//                //return baseResponseModel.getReInfo();
//                throw new ApiException(baseResponseModel);
//            }else{
//                // 如果 {result: true}
//                return baseResponseModel.getReData();
//            }
//        }
//    }

//    public Observable<CoamResponseModel.AAAAA><CsCheckAppUpdateModel> postCheckAppUpdate(String client_id, String secret) {
//        return coamAPI.postCheckAppUpdate(client_id, secret);
//    }


    /*********
     * T - OkHttpCliet
     *****************************************************************************************************************************************************************************************************************************************/
    // OKHttpRequest... /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 用于 Stethoscope 调试的 HttpClient
     */
    static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s

    // https://github.com/wikimedia/apps-android-wikipedia/blob/master/app/src/main/java/org/wikipedia/OkHttpConnectionFactory.java
    private static final long HTTP_CACHE_SIZE = 16 * 1024 * 1024;

    // Cookie 管理
    //private static final CookiesManager cookiesManager = new CookiesManager();

    //private static final OkHttpClient client = new OkHttpClient();
    //client.networkInterceptors().add(new StethoInterceptor());
    public static final OkHttpClient okHttpClient = getOkHttpClient();

    private static OkHttpClient getOkHttpClient() {
        ///OkHttpClient client = new OkHttpClient();
        ///#client.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        ///#client.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        //自定义 OkHttp 拦截器
        //client.interceptors().add(new Base64EncodeRequestInterceptor());//添加验证处理拦截器
        ///client.interceptors().add(new CoamAuthInterceptor());//添加验证处理拦截器

        //选择是否开启 Stetho 网络调试模式
        ///client.networkInterceptors().add(new StethoInterceptor());

        // 启用 Cookie,防止请求 Session_Id 丢失问题
        // http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
        ///client.setCookieHandler(new CookieManager(new O_PersistentCookieStore(CoamApplicationLoader.appContextInstance), CookiePolicy.ACCEPT_ALL));


        // okHttp https://bng86.gitbooks.io/android-third-party-/content/okhttp.html
        // Retrofit2.0 新特性简介 http://mrljdx.com/2016/01/07/Retrofit2-0-%E6%96%B0%E7%89%B9%E6%80%A7%E7%AE%80%E4%BB%8B/
        // OkHttp3源码分析[综述] http://www.jianshu.com/p/aad5aacd79bf
        // okHttp3之Cookies管理及持久化 http://www.geebr.com/post/okHttp3%E4%B9%8BCookies%E7%AE%A1%E7%90%86%E5%8F%8A%E6%8C%81%E4%B9%85%E5%8C%96


        // TODO: Remove when https://github.com/square/okhttp/issues/2543 is fixed.
        // https://github.com/wikimedia/apps-android-wikipedia/commit/058ed9cb5f4b77411f113cb31820e4a8687ee11a
        // https://github.com/square/okhttp/issues/2543
        List<Protocol> protocolList = new ArrayList<Protocol>();
        protocolList.add(Protocol.SPDY_3);
        protocolList.add(Protocol.HTTP_1_1);

        OkHttpClient okClient = new OkHttpClient.Builder()
                // [http协议Authorization认证方式在Android开发中的使用](http://blog.csdn.net/u012702547/article/details/53148706)
                // [Recipes](https://github.com/square/okhttp/wiki/Recipes)
//                .authenticator(new Authenticator() {
//                    @Override
//                    public Request authenticate(Route route, Response response) throws IOException {
//                        System.out.println("Authenticating for response: " + response);
//                        System.out.println("Challenges: " + response.challenges());
//                        //第一个参数为用户名,第二个参数为密码 - Base64Util 编码
//                        String authName = "jesse";
//                        String authPass = "";
//                        if (authName.equals("") || authPass.equals("")) return null;
//                        String credential = Credentials.basic(authName, authPass);
//                        // 如果 Basic 认证失败,判断认证信息是否重复,避免重试发送死循环
//                        if (credential.equals(response.request().header("Authorization"))) {
//                            return null; // If we already failed with these credentials, don't retry.
//                        }
//                        // 限定失败最大尝试次数
//                        if ((new HttpUtils()).responseCount(response) >= 3) {
//                            return null; // If we've failed 3 times, give up.
//                        }
//                        // 尝试发起 Basic 认证
//                        return response.request().newBuilder()
//                                .header("Authorization", credential)
//                                .build();
//                    }
//                })
                ///.protocols(protocolList)
                .cache(new Cache(CoamApplicationLoader.appContextInstance.getCacheDir(), HTTP_CACHE_SIZE))
                // 1)...
                //.cookieJar(cookiesManager) // 适用于多域名 Cookie 自动管理
                // 2)...
                //[Handle Cookies easily with Retrofit/OkHttp](https://gist.github.com/tsuharesu/cbfd8f02d46498b01f1b)
                //[Retrofit2/OkHttp3 Cookies (Drag and Drop, One Size Fits 99%)](https://gist.github.com/nikhiljha/52d45ca69a8415c6990d2a63f61184ff)
                .addInterceptor(new CoamCookiesInterceptor(CoamApplicationLoader.appContextInstance)) // VERY VERY IMPORTANT
                .addInterceptor(new CoamAuthInterceptor())
                .addNetworkInterceptor(new StethoInterceptor())
                // [How to set connection timeout with OkHttp](https://stackoverflow.com/questions/25953819/how-to-set-connection-timeout-with-okhttp)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        // okClient.interceptors().add(new MyInterceptors());
        // okClient.interceptors().add(new CoamAuthInterceptor());
        // okClient.networkInterceptors().add(new StethoInterceptor());

        return okClient;
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    // 根据键名获取对应 Cookie 的值 [仅限: PHPSESSID]...
    public static String getOkHttpClientCookie(String cUrl, String cKey) {
        if (cUrl == null)
            return null;//cUrl = "mpi." + CoamHttpService.MAIN_TESTER_API_SERVER_DOMAIN;

        // 第一种方式...
//        if (HttpUtils.okHttpClient.cookieJar() instanceof CookiesManager) {
//            List<Cookie> common_cookies = ((CookiesManager) HttpUtils.okHttpClient.cookieJar()).getPersistentCookieStore().getCookies();
//
//            for (Cookie item : common_cookies) {
//                ///Timber.i("CCCC:CoamHttpService-okHttpClient|[All]-url:" + "url" + "|item.name:" + item.name() + "|item.value:" + item.value() + "|item.toString:" + item.toString());
//            }
//        }

        // 第二种方式...
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/HttpUrl.html
//        HttpUrl coam_url = new HttpUrl.Builder()
//                .scheme("https")
//                .host(cUrl)
////                    .addPathSegment("search")
////                    .addQueryParameter("q", "polar bears")
//                .build();
//        System.out.println("CCCC:CoamHttpService-okHttpClient:HttpUrl:" + coam_url);
//        if (okHttpClient.cookieJar() instanceof CookiesManager) {
//            List<Cookie> coam_cookies = ((CookiesManager) okHttpClient.cookieJar()).getPersistentCookieStore().get(coam_url);
////            List<Cookie> coam_cookies = ((CookiesManager) okHttpClient.cookieJar()).getPersistentCookieStore().getCookies();
//
//            for (Cookie item : coam_cookies) {
//                Timber.i("CCCC:CoamHttpService-okHttpClient|[mas.coopens.com]-url:" + "url" + "|item.name:" + item.name() + "|item.value:" + item.value() + "|item.toString:" + item.toString());
//                if (item.name().equals(cKey)) return item.value();
//            }
//        }


        // 第三种方式...
        // String rHost = oRequestUrl.host();
        // [Get root domain from request](http://stackoverflow.com/questions/17241532/get-root-domain-from-request)
        String crossRootDomain = cUrl.replaceAll(".*\\.(?=.*\\.)", "");
        Timber.i(String.format(T_Hts_TAG + "|[Start]-Sending request [tag:%s]on %n%s", cUrl, crossRootDomain));
        HashSet<String> preferences = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(CoamApplicationLoader.appContextInstance).getStringSet(crossRootDomain, new HashSet<String>());

        // Use the following if you need everything in one line.
        // Some APIs die if you do it differently.
        String cookiestring = "";
        for (String cookie : preferences) {
            Timber.i(T_Hts_TAG + "|[Loop]:cookie:" + cookie);

            // [Java正则表达式的模式匹配示例](https://my.oschina.net/zzw922cn/blog/487436)
            // [最全的常用正则表达式大全——包括校验数字、字符、一些特殊的需求等等](http://www.cnblogs.com/zxin/archive/2013/01/26/2877765.html)
            // [session_id](http://php.net/manual/zh/function.session-id.php)
            //String ps = "PHPSESSID=p9iu6m6re0pg32japosq2j2ej7; path=/; domain=.coam.co";// ["^PHPSESSID=([-,A-Za-z0-9]{26});.*"][.*; PHPSESSID=([-,A-Za-z0-9]{26});.*"]
            // String ps = "path=/; PHPSESSID=p9iu6m6re0pg32japosq2j2ej7; domain=.coam.co";// [.*; PHPSESSID=([-,A-Za-z0-9]{26});.*"]
            String ps = cookie;
            String psRegex = "^PHPSESSID=([-,A-Za-z0-9]{26});.*";

            Pattern psPattern = Pattern.compile(psRegex);
            Matcher psMatcher = psPattern.matcher(ps);
            if (psMatcher.matches()) {
                String _ps = psMatcher.group(0);
                String ps_id = psMatcher.group(1);
                System.out.println("||||||||||||||||||_ps:" + _ps + "|ps_id:" + ps_id);

                // 返回匹配的 [PHPSESSID:p9iu6m6re0pg32japosq2j2ej7]
                return ps_id;
            }

//            String[] parser = cookie.split(";");
//            cookiestring = cookiestring + parser[0] + "; ";
//            for (String ps : parser) {
//                Timber.i(T_Hts_TAG + "|[Loop!]:ps:" + ps);
//            }
        }

        return null;
    }

    /*********
     * T - RequestToken
     *****************************************************************************************************************************************************************************************************************************************/
    // 获取访问 Token
    // [最简实例说明wait、notify、notifyAll的使用方法](http://longdick.iteye.com/blog/453615)
    // [pause:已暂停|waiting:等待中]
    private final static String http_request_token_flag[] = {"pause"};
    private final static String T_Token_TAG = "TTTTTT|TATATA";

    public static void coamRequestToken(String request_way) {

        // 获取 全局 applicationContext
        Context context = CoamApplicationLoader.appContextInstance;

        Timber.w(T_Token_TAG + "|[Start(request_way:" + request_way + ")]|flag-state:" + http_request_token_flag[0]);

        synchronized (http_request_token_flag) {

            while (http_request_token_flag[0] == "waiting") {
                Timber.w(T_Token_TAG + "|[while:in(start)]" + " begin waiting!");
                long waitTime = System.currentTimeMillis();
                try {
                    http_request_token_flag.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Timber.w(T_Token_TAG + "|[while:in(return)]" + "wait time :" + waitTime);
                return;
            }
            Timber.w(T_Token_TAG + "|[while:out(end)]" + " end waiting!");
        }
        Timber.w(T_Token_TAG + "|000|synchronized[set-flag:waiting]................");
        http_request_token_flag[0] = "waiting";

        final Map<String, String> sParamList = new HashMap<String, String>();
        sParamList.put("pv", "android-1");
        sParamList.put("HHH", "HHH:" + System.currentTimeMillis());
        sParamList.put("request_way", request_way);
        switch (request_way) {
            case "create_token":
                // TODO ...
                break;
            case "refresh_token":
                // TODO ...
                sParamList.put("refresh_token", CoamHttpService.header_auth_token);
                break;
            default:
                break;
        }

        // other verify param
        sParamList.put("runPlatform", "Android");
        sParamList.put("appVersion", CommonUtils.getVersionName(context));
        sParamList.put("requestAccessTokenCode", "ff");

        Call<CoamBaseResponse<CoamResponseModel.WwwCsRequestTokenModel>> sCall = CoamHttpService.getInstance(MPI_SERVER).requestToken(sParamList);

        try {
            retrofit2.Response<CoamBaseResponse<CoamResponseModel.WwwCsRequestTokenModel>> sResponse = sCall.execute();
            if (sResponse.isSuccessful()) {
                CoamBaseResponse<CoamResponseModel.WwwCsRequestTokenModel> sModel = sResponse.body();

                // 更新全局 token
                String access_token = sModel.re_data.access_token;
                String eatToken = null;
                String atWay = sModel.re_data.atWay; // Bearer
                String atType = sModel.re_data.atType;
                switch (request_way) {
                    case "create_token":
                        // TODO ...
                        eatToken = sModel.re_data.eaToken;
                        break;
                    case "refresh_token":
                        // TODO ...
                        eatToken = sModel.re_data.erToken;
                        break;
                    default:
                        break;
                }
                Timber.i("=============================================");
                // “.”和“|”都是转义字符,必须得加"\\";
                String[] datTokenInfo = eatToken.split("\\.");
                String header = datTokenInfo[0];
                String payload = datTokenInfo[1];
                String signature = datTokenInfo[2];
                Timber.i("[datTokenInfo: " + datTokenInfo.toString() + "]");
                Timber.i("[header: " + header + "][payload: " + payload + "][signature: " + signature + "]");

                // 使用公钥解密签名...
                //PublicKey publicKey = RsaUtils.loadPublicKey(CoamBuildVars.PUBLIC_KEY);
                //PrivateKey privateKey = RsaUtils.loadPrivateKey(CoamBuildVars.PRIVATE_KEY);
                PublicKey publicKey = RsaUtils.loadPublicKey(CoamApplicationLoader.appContextInstance, CoamBuildVars.publicCaKf);
                PrivateKey privateKey = RsaUtils.loadPrivateKey(CoamApplicationLoader.appContextInstance, CoamBuildVars.privateCaKf);

                // 使用公钥解密真实签名...
                signature = RsaUtils.decryptDataToStr(signature.getBytes("UTF-8"), publicKey);

                String datToken = header + "." + payload + "." + signature;
                saveCoamTokenInfo(atType, datToken);

                new Handler(context.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // 提示 Token 更新信息...
                        if (BuildConfig.DEBUG) ToastHelper.makeText(context, "[调试]_恭喜,Token更新成功", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.INFO).show();
                    }
                });
                Timber.w(T_Token_TAG + "|try[success]|[access_token:" + access_token + "]................");
                Timber.w(T_Token_TAG + "|try[success]|[eatToken:" + eatToken + "]................");
                Timber.w(T_Token_TAG + "|try[success]|[datToken:" + datToken + "]................");
            } else {
                // 提示 Token 更新信息...
                ToastHelper.makeText(context, "抱歉,Token 获取失败,请稍后尝试...", ToastHelper.LENGTH_LONG, ToastHelper.ToastType.ERROR).show();
                Timber.w(T_Token_TAG + "|try[failed]|0................");
            }

            Timber.w(T_Token_TAG + "|try[end]|0................");
        } catch (IOException e) {
            e.printStackTrace();
            Timber.w(T_Token_TAG + "|try[IOException]|00................|Error:" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Timber.w(T_Token_TAG + "|try[Exception]|000................|Error:" + e.getMessage());
        }

        // 重新初始化 Token 请求状态
        synchronized (http_request_token_flag) {
            http_request_token_flag[0] = "pause";
            http_request_token_flag.notifyAll();
        }
        Timber.w(T_Token_TAG + "|[End]|synchronized[set-flag:pause]................");
    }

    public static void saveCoamTokenInfo(String tt, String token) {
        Context context = CoamApplicationLoader.appContextInstance;

        CoamHttpService.header_auth_token = token;
        CoamHttpService.header_auth_tt = tt;
        SharedPreferences sharedPreferences = context.getSharedPreferences(CoamBuildVars.SP_CONFIG, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(CoamBuildVars.HEADER_AUTH_TOKEN, token).commit();
        sharedPreferences.edit().putString(CoamBuildVars.HEADER_AUTH_TT, tt).commit();
    }

    public static void loadCoamTokenInfo() {
//        if (CoamHttpService.header_auth_token != null) {
//            return CoamHttpService.header_auth_token;
//        }
        Context context = CoamApplicationLoader.appContextInstance;
        SharedPreferences sharedPreferences = context.getSharedPreferences(CoamBuildVars.SP_CONFIG, Context.MODE_PRIVATE);
        CoamHttpService.header_auth_token = sharedPreferences.getString(CoamBuildVars.HEADER_AUTH_TOKEN, null);
        CoamHttpService.header_auth_tt = sharedPreferences.getString(CoamBuildVars.HEADER_AUTH_TT, null);
        //return CoamHttpService.header_auth_token;
    }

    /*********
     * WWW - CommonService
     *****************************************************************************************************************************************************************************************************************************************/
    // 获取访问 Token
    @Override
    public Call<CoamBaseResponse<CoamResponseModel.WwwCsRequestTokenModel>> requestToken(@FieldMap Map<String, String> parameter) {
        return mCoamApi.requestToken(parameter);
        ////.map(new HttpResultFunc<CoamResponseModel.MainCsRequestTokenModel>())
        //.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 获取图形验证码
    @Override
    public Call<ResponseBody> loadAuthCodeVerifyImg(@QueryMap Map<String, String> parameter) {
        return mCoamApi.loadAuthCodeVerifyImg(parameter);
        ////.map(new HttpResultFunc<CoamResponseModel.MainCsRequestTokenModel>())
        //.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }


    // 校验图形验证码
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.WwwCsVerifyAuthCodeModel>> verifyAuthCode(@FieldMap Map<String, String> parameter) {
        return mCoamApi.verifyAuthCode(parameter)
                //.map(new HttpResultFunc<CoamResponseModel.MainCsSendAuthCodeVerifyCodeModel>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    /*********
     * APP - AjaxLoad
     *****************************************************************************************************************************************************************************************************************************************/
    //获取全国省市区 区域列表
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.WwwCsRunAreaInfoModel>> runAreaInfo(@FieldMap Map<String, String> parameter) {
        return mCoamApi.runAreaInfo(parameter)
                //.map(new HttpResultFunc<CoamResponseModel.AmSendPhoneSMSVerifyCodeModel>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    /*********
     * APP - AjaxManager
     *****************************************************************************************************************************************************************************************************************************************/
    // 获取短信验证码
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.WwwCsGetSecurityCodeModel>> getSecurityCode(@FieldMap Map<String, String> parameter) {
        return mCoamApi.getSecurityCode(parameter)
                //.map(new HttpResultFunc<CoamResponseModel.AlSendPhoneSmsVerifyCodeModel>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    /*********
     * APP - CloudStorage
     *****************************************************************************************************************************************************************************************************************************************/
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CCreateCloudFileUpTokenModel>> createCloudFileUpToken(Map<String, String> parameter) {
        return mCoamApi.createCloudFileUpToken(parameter)
                //.map(new HttpResultFunc<CoamResponseModel.CGetCloudUploadTokenModel>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    /*********
     * APP - CommonAccountManager
     *****************************************************************************************************************************************************************************************************************************************/
    // 使用普通手机账号密码登陆账号
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel>> sendLoginAccount(@Path("loginWay") String loginWay, @FieldMap Map<String, String> parameter) {
        return mCoamApi.sendLoginAccount(loginWay, parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendLoginAccountModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 修改密码
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendRetrievePasswordModel>> sendRetrievePassword(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendRetrievePassword(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendRetrievePasswordModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 使用 SNS 第三方接口登陆账户
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendLoginSnsModel>> sendLoginSns(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendLoginSns(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendLoginSnsModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //绑定第三方账号
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendSnsBindAccountModel>> sendSNSBindAccount(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendSNSBindAccount(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendSnsBindAccountModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //解除第三方账号绑定
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiRemoveSnsAccountBindModel>> removeSNSAccountBind(@FieldMap Map<String, String> parameter) {
        return mCoamApi.removeSNSAccountBind(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiRemoveSnsAccountBindModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //注册
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendCommonAccountInfoRegisterModel>> sendCommonAccountInfoRegister(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendCommonAccountInfoRegister(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendCommonAccountInfoRegisterModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //获取账户信息
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSearchAccountInfoModel>> searchAccountInfo(@FieldMap Map<String, String> parameter) {
        return mCoamApi.searchAccountInfo(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSearchAccountInfoModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //修改账户信息(帐户名,手机号,邮箱)
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiUpdateCommonAccountInfoModel>> updateAdminAccountInfo(@FieldMap Map<String, String> parameter) {
        return mCoamApi.updateAdminAccountInfo(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiUpdateCommonAccountInfoModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //发送验证邮箱
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiSendAccountAuthEmailModel>> sendAccountAuthEmail(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendAccountAuthEmail(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiSendAccountAuthEmailModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //修改账户信息(昵称,个性签名,是否开启消息推送,位置权限)
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CaiUpdateAccountInfoModel>> updateAppRunInfo(@FieldMap Map<String, String> parameter) {
        return mCoamApi.updateAppRunInfo(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CaiUpdateAccountInfoModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    /*********
     * APP - CommonService
     *****************************************************************************************************************************************************************************************************************************************/
    //搜索历史
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsSearchMyHsAreaListModel>> searchMyHsAreaList(@FieldMap Map<String, String> parameter) {
        return mCoamApi.searchMyHsAreaList(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsSearchMyHsAreaListModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //定时发送经纬度
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsSendPoModel>> sendPo(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendPo(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsSendPoModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //检查手机软件更新
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>> checkAppUpdate(@FieldMap Map<String, String> parameter) {
        return mCoamApi.checkAppUpdate(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 注册手持设备...
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppDeviceRegisterModel>> appDeviceRegister(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appDeviceRegister(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 注册手持设备...
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppBeatHeartModel>> appBeatHeart(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appBeatHeart(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // 数据上报
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppGateDataReportModel>> appGateDataReport(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appGateDataReport(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // Ping 网络
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppGatePingModel>> appGatePing(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appGatePing(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // App 更新密钥
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppLoadEncryptKeyModel>> appLoadEncryptKey(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appLoadEncryptKey(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // App 查询有效入园次数
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppQueryEnterCountModel>> appQueryEnterCount(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appQueryEnterCount(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    // App 人脸识别
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsAppFaceCompareModel>> appFaceCompare(@FieldMap Map<String, String> parameter) {
        return mCoamApi.appFaceCompare(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsCheckAppUpdateModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //投诉
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsSendComplaintsSuggestionsModel>> sendComplaintsSuggestions(@FieldMap Map<String, String> parameter) {
        return mCoamApi.sendComplaintsSuggestions(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsSendComplaintsSuggestionsModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }

    //检索身份证编号缓存
    @Override
    public Observable<CoamBaseResponse<CoamResponseModel.CsCheckIdCodeModel>> checkIdCode(@FieldMap Map<String, String> parameter) {
        return mCoamApi.checkIdCode(parameter)
                //.map(new HttpResultFunc<CoamBaseResponse<CoamResponseModel.CsSendComplaintsSuggestionsModel>>())
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread());
    }
    /** The end... *****************************************************************************************************************************************************************************************************************************/
}
