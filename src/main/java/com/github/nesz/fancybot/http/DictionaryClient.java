package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
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

public class DictionaryClient extends HTTPClient {

    private static final String HOST = "api.urbandictionary.com";
    private static final String PATH_DEFINITION = "v0/define";

    public DictionaryClient() {

    }

    public JSONArray getDefinitions(String query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_DEFINITION)
                .addQueryParameter("term", query)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return null;
            }
            JSONObject jsonResponse = new JSONObject(response.body().string());
            if (!jsonResponse.has("list")) {
                return null;
            }
            JSONArray definitions = jsonResponse.getJSONArray("list");
            if (definitions.isEmpty()) {
                return null;
            }
            return definitions;
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[DictionaryClient] An error occurred while searching for definitions!", e);
            return null;
        }
    }


}
