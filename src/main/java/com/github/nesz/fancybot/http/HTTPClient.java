package com.github.nesz.fancybot.http;

import okhttp3.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

public abstract class HTTPClient {

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .addNetworkInterceptor(provideCacheInterceptor())
            .cache(new Cache(new File("cache"), 200 * 1024 * 1024)) //200mb
            .followRedirects(false)
            .retryOnConnectionFailure(false)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(20, 60, TimeUnit.SECONDS))
            .build();

    private static Interceptor provideCacheInterceptor () {
        return chain -> {
            Response response = chain.proceed( chain.request() );
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(3, TimeUnit.DAYS)
                    .build();

            return response.newBuilder()
                    .header("Cache-Control", cacheControl.toString() )
                    .build();
        };
    }
}
