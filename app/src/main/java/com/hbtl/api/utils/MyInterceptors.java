package com.hbtl.api.utils;

/**
 * Created by yafei on 2/11/2016.
 */

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyInterceptors implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        //封装headers
        Request request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json") //添加请求头信息
                .build();
        Headers headers = request.headers();
        //打印
        System.out.println("Content-Type is : " + headers.get("Content-Type"));
        String requestUrl = request.url().toString(); //获取请求url地址
        String methodStr = request.method(); //获取请求方式
        RequestBody body = request.body(); //获取请求body
        String bodyStr = (body == null ? "" : body.toString());
        //打印Request数据
        System.out.println("Request Url is :" + requestUrl + "\nMethod is : " + methodStr + "\nRequest Body is :" + bodyStr + "\n");

        Response response = chain.proceed(request);
        if (response != null) {
            System.out.println("Response is not null");
        } else {
            System.out.println("Respong is null");
        }
        return response;

    }
}