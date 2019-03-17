package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.utils.AudioUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class YouTubeClient extends HTTPClient {

    private static final String SEARCH_FIELDS = "items(id/videoId)";
    private static final String VIDEO_FIELDS
            = "items(id/videoId,contentDetails/duration,snippet/title,snippet/channelTitle,snippet/liveBroadcastContent)";
    private static final String RELATED_FIELDS = "items(id,snippet/title,snippet/channelTitle,snippet/liveBroadcastContent)";

    private static final String HOST   = "www.googleapis.com";
    private static final String PATH_SEARCH = "youtube/v3/search";
    private static final String PATH_VIDEOS = "youtube/v3/videos";

    private final String token;

    public YouTubeClient(String token) {
        this.token = token;
    }

    public AudioTrack getRelatedVideo(String id) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_SEARCH)
                .addQueryParameter("relatedToVideoId", id)
                .addQueryParameter("part", "snippet")
                .addQueryParameter("maxResults", "1")
                .addQueryParameter("type", "video")
                .addEncodedQueryParameter("fields", RELATED_FIELDS)
                .addQueryParameter("key", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
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
            FancyBot.LOG.debug(items);

            JSONObject trackData = items.getJSONObject(0);
            String trackId = trackData.getJSONObject("id").getString("videoId");

            return AudioUtils.buildTrack(
                    trackId,
                    trackData.getJSONObject("snippet").getString("title"),
                    trackData.getJSONObject("snippet").getString("channelTitle"),
                    trackData.getJSONObject("snippet").getString("liveBroadcastContent").contains("live"),
                    durationForVideos(Collections.singletonList(trackId)).get(trackId)
            );
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while searching related video!", e);
            return null;
        }
    }

    public String getFirstID(String query) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_SEARCH)
                .addQueryParameter("q", query)
                .addQueryParameter("key", token)
                .addQueryParameter("part", "snippet")
                .addQueryParameter("maxResults", "1")
                .addEncodedQueryParameter("fields", SEARCH_FIELDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
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
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while searching video!", e);
            return null;
        }
    }

    private Map<String, Long> durationForVideos(List<String> ids) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_VIDEOS)
                .addQueryParameter("part", "contentDetails")
                .addEncodedQueryParameter("id", String.join(",", ids))
                .addQueryParameter("key", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return Collections.emptyMap();
            }
            JSONObject object = new JSONObject(response.body().string());
            if (!object.has("items")) {
                return Collections.emptyMap();
            }
            JSONArray items = object.getJSONArray("items");
            if (items.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Long> map = new HashMap<>();
            for (int i = 0 ; i < items.length(); i++) {
                JSONObject current = items.getJSONObject(i);
                map.put(current.getString("id"), toLongDuration(current.getJSONObject("contentDetails").getString("duration")));
            }
            return map;
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while getting videos duration!", e);
            return Collections.emptyMap();
        }
    }

    public JSONObject getVideo(String id) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST)
                .addPathSegments(PATH_VIDEOS)
                .addQueryParameter("id", id)
                .addQueryParameter("key", token)
                .addEncodedQueryParameter("part", "contentDetails,snippet")
                .addEncodedQueryParameter("fields", VIDEO_FIELDS)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = asyncRequest(request).get(30, TimeUnit.SECONDS)) {
            if (response.body() == null) {
                return null;
            }
            JSONArray items = new JSONObject(response.body().string()).getJSONArray("items");
            if (items.isEmpty()) {
                return null;
            }
            return items.getJSONObject(0);
        } catch (IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e) {
            FancyBot.LOG.error("[YouTubeClient] An error occurred while getting video!", e);
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
