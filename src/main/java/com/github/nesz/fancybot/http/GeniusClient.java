package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GeniusClient extends HTTPClient
{

    private static final String HOST = "api.genius.com";
    private static final String PATH_SEARCH = "search";

    private final String token;

    public GeniusClient(final String token)
    {
        this.token = token;
    }

    public HTTPResponse<JSONObject> getTopSearch(final String query)
    {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegment(PATH_SEARCH)
                .addQueryParameter("q", query)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {

            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final JSONObject hits = new JSONObject(response.body().string())
                    .getJSONObject("response")
                    .getJSONArray("hits")
                    .getJSONObject(0)
                    .getJSONObject("result");

            if (hits == null || hits.isEmpty())
            {
                return new HTTPResponse<>(response.code(), null);
            }


            return new HTTPResponse<>(response.code(), hits);

        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOGGER.error("[GeniusClient] An error occurred while searching for lyrics!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

    public HTTPResponse<String> getLyrics(final String url)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {

            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final Document document = Jsoup.parse(response.body().string());
            final Elements elements = document.select("div.lyrics");

            if (!elements.hasText())
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final String lyrics = Jsoup.clean(elements.html(), Whitelist.none().addTags("br"))
                    .trim()
                    .replace("<br> ", "");

            return new HTTPResponse<>(response.code(), lyrics);

        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return new HTTPResponse<>(-1, null);
        }
    }
}
