package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HastebinClient extends HTTPClient
{

    private static final String WATCH_URL = "https://hastebin.com/";
    private static final String HOST = "hastebin.com";
    private static final String PATH_POST = "documents";

    public HastebinClient()
    {

    }


    public HTTPResponse<String> post(final String post)
    {
        final RequestBody body = RequestBody.create(null, post);

        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegment(PATH_POST)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {
            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final String key = new JSONObject(response.body().string()).getString("key");

            if (key == null) {
                return new HTTPResponse<>(response.code(), null);
            }

            return new HTTPResponse<>(response.code(), WATCH_URL + key);
        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[HastebinClient] An error occurred while posting data!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

}
