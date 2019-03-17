package com.github.nesz.fancybot.http.basic;

import com.github.nesz.fancybot.FancyBot;
import okhttp3.*;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public abstract class HTTPClient {

    protected static final String SCHEME_HTTPS = "https";

    protected static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .addNetworkInterceptor(provideCacheInterceptor())
            .cache(new Cache(new File("cache"), 200 * 1024 * 1024)) //200mb
            .followRedirects(false)
            .retryOnConnectionFailure(false)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(20, 60, TimeUnit.SECONDS))
            .build();

    private static Interceptor provideCacheInterceptor () {
        return chain -> {
            Response response = chain.proceed(chain.request());
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(3, TimeUnit.DAYS)
                    .build();

            return response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build();
        };
    }

    protected static Future<Response> asyncRequest(Request request) {
        FancyBot.LOG.debug(request.url());
        Call call = HTTP_CLIENT.newCall(request);

        HTTPResponseFuture result = new HTTPResponseFuture();

        call.enqueue(result);

        return result.future;
    }
}
