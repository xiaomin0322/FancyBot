package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.utils.RandomUtil;
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

public class ImgurClient extends HTTPClient {

    private static final String HOST = "api.imgur.com";
    private static final String PATH_REDDIT = "3/gallery/r/";

    private final String key;

    public ImgurClient(String key) {
        this.key = key;
    }

    public String randomRedditImage(String subreddit) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_REDDIT)
                .addPathSegment(subreddit)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Client-ID " + key)
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray.isEmpty()) {
                return null;
            }
            return jsonArray.getJSONObject(RandomUtil.getRandomIntBetween(0, jsonArray.length() - 1)).getString("link");
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[ImgurClient] An error occurred while getting image!", e);
            return null;
        }
    }

}
