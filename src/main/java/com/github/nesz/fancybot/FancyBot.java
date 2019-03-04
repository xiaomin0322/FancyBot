package com.github.nesz.fancybot;

import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.config.loader.ConfigLoader;
import com.github.nesz.fancybot.http.GeniusClient;
import com.github.nesz.fancybot.http.HTTPClient;
import com.github.nesz.fancybot.http.YouTubeClient;
import com.github.nesz.fancybot.listeners.GuildMessageReceivedListener;
import com.github.nesz.fancybot.listeners.ReactionListener;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class FancyBot extends HTTPClient {

    public static final Logger LOG = LogManager.getLogger("FancyBot");

    private static YouTubeClient youTubeClient;
    private static GeniusClient geniusClient;
    private static ShardManager shardManager;

    public static void main(String[] args) {
        new ConfigLoader(Config.class, "config.json").override();
        youTubeClient = new YouTubeClient(Config.YOUTUBE_SECRET);
        geniusClient = new GeniusClient(Config.GENIUS_SECRET);

        try {
            shardManager = new DefaultShardManagerBuilder()
                    .setHttpClient(HTTP_CLIENT)
                    .setToken(Config.BOT_TOKEN)
                    .setShardsTotal(-1)
                    .setGame(Game.watching("?"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(
                            new GuildMessageReceivedListener(),
                            new ReactionListener()
                    ).build();
        }
        catch (LoginException e) {
            LOG.error("Could not initialize shards!", e);
            Runtime.getRuntime().exit(1);
        }

    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static YouTubeClient getYouTubeClient() {
        return youTubeClient;
    }

    public static GeniusClient getGeniusClient() {
        return geniusClient;
    }

    public static String getVersion() {
        String version = FancyBot.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "development";
        }
        return version;
    }
}
