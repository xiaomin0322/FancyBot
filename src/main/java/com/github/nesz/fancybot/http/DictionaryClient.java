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

public class DictionaryClient extends HTTPClient
{

    private static final String HOST = "api.urbandictionary.com";
    private static final String PATH_DEFINITION = "v0/define";

    public DictionaryClient()
    {

    }

    public HTTPResponse<JSONArray> retriveDefinitions(final String query)
    {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_DEFINITION)
                .addQueryParameter("term", query)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {

            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final JSONArray definitions = new JSONObject(response.body().string())
                    .getJSONArray("list");

            if (definitions == null || definitions.isEmpty())
            {
                return new HTTPResponse<>(response.code(), null);
            }

            return new HTTPResponse<>(response.code(), definitions);

        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[DictionaryClient] An error occurred while retrieving for definitions!", e);
            return new HTTPResponse<>(-1, null);
        }
    }


}
