package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
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

public class GeniusClient extends HTTPClient {

    private static final String HOST = "api.genius.com";
    private static final String PATH_SEARCH = "search";

    private final String token;

    public GeniusClient(String token) {
        this.token = token;
    }

    public JSONObject getTopSearch(String query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegment(PATH_SEARCH)
                .addQueryParameter("q", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return null;
            }
            JSONObject jsonResponse = new JSONObject(response.body().string()).getJSONObject("response");
            if (!jsonResponse.has("hits")) {
                return null;
            }
            return jsonResponse.getJSONArray("hits").getJSONObject(0).getJSONObject("result");
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[GeniusClient] An error occurred while searching for lyrics!", e);
            return null;
        }
    }

    public String getLyrics(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return null;
            }
            Document doc = Jsoup.parse(response.body().string());
            Elements lyrics = doc.select("div.lyrics");
            if (!lyrics.hasText()) {
                return null;
            }
            return Jsoup.clean(lyrics.html(), Whitelist.none().addTags("br")).trim().replace("<br> ", "");
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return null;
        }
    }
}
