package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.utils.RandomUtil;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ImgurClient extends HTTPClient {

    private static final String API_REDDIT_URL = "https://api.imgur.com/3/gallery/r/";

    private final String key;

    public ImgurClient(String key) {
        this.key = key;
    }

    public String randomRedditImage(String subreddit) {
        Request request = new Request.Builder()
                .url(API_REDDIT_URL + subreddit)
                .addHeader("Authorization", "Client-ID " + key)
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray.isEmpty()) {
                return null;
            }
            return jsonArray.getJSONObject(RandomUtil.getRandomIntBetween(0, jsonArray.length() - 1)).getString("link");
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[GeniusClient] An error occurred while getting lyrics!", e);
            return null;
        }
    }

}
