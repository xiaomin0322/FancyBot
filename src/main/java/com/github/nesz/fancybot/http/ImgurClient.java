package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ImgurClient extends HTTPClient
{

    private static final String HOST = "api.imgur.com";
    private static final String PATH_REDDIT = "3/gallery/r/";

    private final String key;

    public ImgurClient(final String key)
    {
        this.key = key;
    }

    public HTTPResponse<JSONArray> retrieveRedditImages(final String subreddit)
    {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_REDDIT)
                .addPathSegment(subreddit)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Client-ID " + key)
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {

            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final JSONArray data = new JSONObject(response.body().string())
                    .getJSONArray("data");

            if (data == null || data.isEmpty())
            {
                return new HTTPResponse<>(response.code(), null);
            }


            return new HTTPResponse<>(response.code(), data);

        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[ImgurClient] An error occurred while getting image!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

}
