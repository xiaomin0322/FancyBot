package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class YouTubeClient extends HTTPClient {

    private static final String SEARCH_FIELDS = "items(id/videoId)";
    private static final String VIDEO_FIELDS
            = "items(id,contentDetails/duration,snippet/title,snippet/channelTitle,snippet/liveBroadcastContent)";
    private static final String API_SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
    private static final String API_VIDEO_URL = "https://www.googleapis.com/youtube/v3/videos";

    private final String token;

    public YouTubeClient(String token) {
        this.token = token;
    }

    public String getFirstID(String query) {
        Request request = new Request.Builder()
                .url(API_SEARCH_URL + "?q=" + query + "&key=" + token + "&part=snippet" + "&maxResults=1" + "&fields=" + SEARCH_FIELDS)
                .get()
                .build();

        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            JSONObject object = new JSONObject(response.body().string());
            if (!object.has("items")) {
                return null;
            }
            JSONArray items = object.getJSONArray("items");
            if (items.isEmpty()) {
                return null;
            }
            return items.getJSONObject(0).getJSONObject("id").getString("videoId");
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while searching video!", e);
            return null;
        }
    }

    public JSONObject getVideo(String id) {
        Request request = new Request.Builder()
                .url(API_VIDEO_URL + "?id=" + id + "&key=" + token + "&part=contentDetails,snippet"+ "&fields=" + VIDEO_FIELDS)
                .get()
                .build();
        FancyBot.LOG.debug(API_VIDEO_URL + "?id=" + id + "&key=" + token + "&part=contentDetails,snippet"+ "&fields=" + VIDEO_FIELDS);
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            if (response.body() == null) {
                return null;
            }
            JSONArray items = new JSONObject(response.body().string()).getJSONArray("items");
            if (items.isEmpty()) {
                return null;
            }
            return items.getJSONObject(0);
        } catch (IOException | JSONException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while searching video!", e);
            return null;
        }
    }

    public long toLongDuration(String dur) {
        String time = dur.substring(2);
        long duration = 0L;
        Object[][] indexes = new Object[][] { { "H", 3600 }, { "M", 60 }, { "S", 1 } };
        for (Object[] index1 : indexes) {
            int index = time.indexOf((String) index1[0]);
            if (index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) index1[1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }

}
