package com.github.nesz.fancybot.http;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.http.basic.HTTPClient;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.utils.AudioUtils;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class YouTubeClient extends HTTPClient
{

    private static final String VIDEO_FIELDS
        = "items(id/videoId,contentDetails/duration,snippet/title,snippet/channelTitle,snippet/liveBroadcastContent)";
    private static final String CHANNEL_FIELDS
            = "items(id,statistics/viewCount,statistics/subscriberCount,statistics/videoCount)";

    private static final String HOST_API      = "www.googleapis.com";
    private static final String HOST_WEB      = "www.youtube.com";
    private static final String PATH_VIDEOS   = "youtube/v3/videos";
    private static final String PATH_CHANNELS = "youtube/v3/channels";

    private final String token;

    public YouTubeClient(final String token)
    {
        this.token = token;
    }

    /*
        With youtube API we are able to make only 100 queries so Im scrapping data directly :(
     */
    private static final List<String> REQUIRED_KEYS = Arrays.asList("title", "id", "author", "length_seconds");
    private static final String SCRAPPING_KEYWORD = "'RELATED_PLAYER_ARGS'";
    private static final String SCRAPPING_START = "yt.setConfig(";
    private static final String SCRAPPING_END = ");";

    public HTTPResponse<List<AudioTrack>> retrieveRelatedVideos(final String id)
    {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST_WEB)
                .addPathSegment("watch")
                .addQueryParameter("v", id)
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

            final Document document = Jsoup.parse(response.body().string());
            final Elements elements = document.getElementsByTag("script");

            {
                // data for me as i can fix unusual cases that are not covered :)
                final HTTPResponse<String> post = FancyBot.getHastebinClient().post(document.toString());
                if (post.getData().isPresent())
                {
                    FancyBot.LOGGER.debug("DATA FROM REQUEST: " + post.getData().get());
                }
                else
                    {
                    FancyBot.LOGGER.debug("NO DATA IS PRESENT, RESPONSE CODE: " + post.getResponseCode());
                }
            }

            for (final Element element : elements)
            {
                if (!element.html().contains(SCRAPPING_KEYWORD))
                {
                    continue;
                }

                final String scrap = StringUtils.substringBetween(element.html(), SCRAPPING_START, SCRAPPING_END);

                if (scrap == null)
                {
                    return new HTTPResponse<>(response.code(), null);
                }

                final List<AudioTrack> relatedTracks = new ArrayList<>();

                final String rawData = new JSONObject(scrap)
                        .getJSONObject("RELATED_PLAYER_ARGS")
                        .getString("rvs");

                final List<String> rawDataParts = Arrays.stream(rawData.split(","))
                        .collect(Collectors.toList());

                for (final String video : rawDataParts) {

                    final List<String> data = Arrays.stream(video.split("&"))
                            .collect(Collectors.toList());

                    final Map<String, String> dataMap = data.stream()
                            .map(s -> s.split("=", 2))
                            .filter(s -> s.length == 2)
                            .collect(Collectors.toMap(a -> a[0], a -> a[1]));

                    final boolean hasKeys = REQUIRED_KEYS.stream().allMatch(dataMap::containsKey);

                    if (!hasKeys)
                    {
                        continue;
                    }

                    relatedTracks.add(AudioUtils.buildTrack(
                            dataMap.get("id"),
                            URLDecoder.decode(dataMap.get("title"), "UTF-8"),
                            URLDecoder.decode(dataMap.get("author"), "UTF-8"),
                            false,
                            TimeUnit.SECONDS.toMillis(Long.valueOf(dataMap.get("length_seconds")))
                    ));
                }

                return new HTTPResponse<>(response.code(), relatedTracks);
            }
            return new HTTPResponse<>(response.code(), null);

        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[YouTubeClient] An error occurred while scrapping related videos!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

    public HTTPResponse<JSONObject> retrieveVideo(final String id)
    {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST_API)
                .addPathSegments(PATH_VIDEOS)
                .addQueryParameter("id", id)
                .addQueryParameter("key", token)
                .addEncodedQueryParameter("part", "contentDetails,snippet")
                .addEncodedQueryParameter("fields", VIDEO_FIELDS)
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

            final JSONObject item = new JSONObject(response.body().string()).getJSONArray("items").getJSONObject(0);

            if (item == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            return new HTTPResponse<>(response.code(), item);
        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[YouTubeClient] An error occurred while getting video!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

    public HTTPResponse<JSONArray> retrieveDataForChannels(final List<String> ids) {
        final HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME_HTTPS)
                .host(HOST_API)
                .addPathSegments(PATH_CHANNELS)
                .addQueryParameter("id", String.join(",", ids))
                .addQueryParameter("key", token)
                .addEncodedQueryParameter("part", "statistics")
                .addEncodedQueryParameter("fields", CHANNEL_FIELDS)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .get()
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        try (final Response response = callAsync(request).get(30, TimeUnit.SECONDS))
        {
            if (response.body() == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            final JSONArray items = new JSONObject(response.body().string()).getJSONArray("items");

            if (items == null)
            {
                return new HTTPResponse<>(response.code(), null);
            }

            return new HTTPResponse<>(response.code(), items);
        }
        catch (final IOException | JSONException | InterruptedException | ExecutionException | TimeoutException e)
        {
            FancyBot.LOGGER.error("[YouTubeClient] An error occurred while getting channels data!", e);
            return new HTTPResponse<>(-1, null);
        }
    }

    public long toLongDuration(final String dur)
    {
        String time = dur.substring(2);
        long duration = 0L;
        final Object[][] indexes = new Object[][] { { "H", 3600 }, { "M", 60 }, { "S", 1 } };

        for (final Object[] index1 : indexes)
        {
            final int index = time.indexOf((String) index1[0]);
            if (index != -1)
            {
                final String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) index1[1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }

        return duration;
    }

}
