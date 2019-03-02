package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class YouTubeClient extends HTTPClient {

    private static final String SEARCH_FIELDS = "items(id/videoId)";
    private static final String API_URL = "https://www.googleapis.com/youtube/v3/search";

    private final String token;

    public YouTubeClient(String token) {
        this.token = token;
    }

    public String getFirstID(String query) {
        Request request = new Request.Builder()
                .url(API_URL + "?q=" + query + "&key=" + token + "&part=snippet" + "&maxResults=1" + "&fields=" + SEARCH_FIELDS)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            return new JSONObject(response.body().string()).getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while searching video!", e);
            return null;
        }
    }


}
