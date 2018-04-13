package com.hbtl.api.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import timber.log.Timber;

/**
 * Created by 亚飞 on 2015-10-21.
 * 将所有Post请求的请求体通过 URLEncoder.encode(buffer.toString(), "UTF-8") 的方式编码传送
 * http://stackoverflow.com/questions/33131825/retrofit-how-to-send-base64-encoded-body-request-in-post
 * https://github.com/square/okhttp/wiki/Recipes
 */
public class Base64EncodeRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (originalRequest.method().equalsIgnoreCase("POST")) {
            builder = originalRequest.newBuilder().method(originalRequest.method(), encode(originalRequest.body()));
            Timber.i("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY-");
            Timber.i(originalRequest.body().toString());
            Timber.i("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY+");
        }

//        String rB = bodyToString(originalRequest);
//        Timber.i("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII-Start.");
//        Timber.i(rB);
//        Timber.i("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII-End.");

        return chain.proceed(builder.build());
    }

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    private RequestBody encode(RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

//            @Override public void writeTo(BufferedSink sink) throws IOException {
//                sink.writeUtf8("Numbers\n");
//                sink.writeUtf8("-------\n");
//                for (int i = 2; i <= 997; i++) {
//                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
//                }
//            }
//
//            private String factor(int n) {
//                for (int i = 2; i < n; i++) {
//                    int x = n / i;
//                    if (x * i == n) return factor(x) + " × " + i;
//                }
//                return Integer.toString(n);
//            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                //byte[] encoded = Base64Util.encode(buffer.readByteArray(), Base64Util.DEFAULT);
                //byte[] encoded = buffer.readString(Charset.forName("UTF-8")).getBytes();

                Timber.i("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT...");
                //Timber.i(body.toString());
//                Timber.i(buffer.toString());
                //Timber.i("11");
                //Timber.i(buffer.readUtf8()); //可以读取出Utf-8字符串
//                Timber.i("11");
//                Timber.i(buffer.readByteString());
//                Timber.i("11");
//                Timber.i(buffer.readString(Charset.forName("UTF-8")));
                Timber.i("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE...");

                //Timber.i(buffer.);
                //byte[] encoded =  URLEncoder.encode(buffer.toString(), "UTF-8").getBytes();
                //byte[] encoded = buffer.readUtf8().getBytes();
                //byte[] encoded = URLEncoder.encode(buffer.readUtf8(), "UTF-8").getBytes();
                sink.write(ByteString.encodeUtf8(buffer.readUtf8()));
                //sink.writeUtf8(buffer.readUtf8());
                buffer.close();
                sink.close();
            }
        };
    }
}
