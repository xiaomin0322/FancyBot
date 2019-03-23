package com.github.nesz.fancybot.http.basic;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HTTPResponseFuture implements Callback
{

    final CompletableFuture<Response> future = new CompletableFuture<>();

    HTTPResponseFuture()
    {

    }

    @Override
    public void onFailure(@NotNull final Call call, @NotNull final IOException e)
    {
        future.completeExceptionally(e);
    }

    @Override
    public void onResponse(@NotNull final Call call, @NotNull final Response response)
    {
        future.complete(response);
    }
}
