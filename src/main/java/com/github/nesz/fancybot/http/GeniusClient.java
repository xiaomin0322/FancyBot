package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GeniusClient extends HTTPClient {

    private static final String API_URL = "https://api.genius.com";

    private final String token;

    public GeniusClient(String token) {
        this.token = token;
    }

    public String search(String query) {
        Request request = new Request.Builder()
                .url(API_URL + "/search?q=" + query)
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            return new JSONObject(response.body().string()).getJSONObject("response").getJSONArray("hits").getJSONObject(0).getJSONObject("result").getString("url");
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[GeniusClient] An error occurred while searching for lyrics!", e);
            return null;
        }
    }

    public String getLyrics(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return "Something went wrong while fetching lyrics ...";
            }
            Document doc = Jsoup.parse(response.body().string());
            Elements lyrics = doc.select("div.lyrics");
            if (!lyrics.hasText()) {
                return "Something went wrong while fetching lyrics ...";
            }
            return Jsoup.clean(lyrics.html(), Whitelist.none().addTags("br")).trim().replace("<br> ", "");
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return null;
        }
    }
}
