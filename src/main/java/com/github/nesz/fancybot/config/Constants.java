package com.github.nesz.fancybot.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class Constants
{

    public static final Cache<String, String> IGNORED_TRACKS = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(2000)
            .build();

    public static final int PLAYLIST_MAX_SIZE = 500;
    public static final int PLAYLIST_NAME_LENGTH_MAX = 32;
    public static final int PLAYLIST_NAME_LENGTH_MIN = 3;
    public static final int MAX_QUEUE_SIZE = 500;
    public static final String VERSION;

    static
    {
        final String version = Constants.class.getPackage().getImplementationVersion();
        if (version == null) VERSION = "development";
        else VERSION = version;
    }
}
