package com.github.nesz.fancybot;

import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.config.loader.ConfigLoader;
import com.github.nesz.fancybot.http.GeniusClient;
import com.github.nesz.fancybot.http.ImgurClient;
import com.github.nesz.fancybot.http.YouTubeClient;
import com.github.nesz.fancybot.listeners.GuildMessageReceivedListener;
import com.github.nesz.fancybot.listeners.GuildVoiceListener;
import com.github.nesz.fancybot.listeners.ReactionListener;
import com.github.nesz.fancybot.objects.database.Database;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.utils.FileUtils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class FancyBot {

    public static final Logger LOG = LogManager.getLogger("FancyBot");

    private static Database database;
    private static YouTubeClient youTubeClient;
    private static GeniusClient geniusClient;
    private static ImgurClient imgurClient;
    private static ShardManager shardManager;

    public static void main(String[] args) {
        FileUtils.saveFile("config.json");
        FileUtils.saveDir("lang", Arrays.asList("pl_PL.properties", "en_US.properties"));


        new ConfigLoader(Config.class, "config.json").override();
        database = new Database();
        youTubeClient = new YouTubeClient(Config.YOUTUBE_SECRET);
        geniusClient = new GeniusClient(Config.GENIUS_SECRET);
        imgurClient = new ImgurClient(Config.IMGUR_TOKEN);

        try {
            shardManager = new DefaultShardManagerBuilder()
                    .setToken(Config.BOT_TOKEN)
                    .setShardsTotal(-1)
                    .setActivity(Activity.watching("?"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setAutoReconnect(true)
                    .addEventListeners(
                            new GuildMessageReceivedListener(),
                            new GuildVoiceListener(),
                            new ReactionListener()
                    ).build();
        }
        catch (LoginException e) {
            LOG.error("Could not initialize shards!", e);
            Runtime.getRuntime().exit(1);
        }

        database.executeUpdate(Queries.CREATE_TABLE_DATA);
        database.executeUpdate(Queries.CREATE_TABLE_TRACKS);
        database.executeUpdate(Queries.CREATE_TABLE_GUILD_DATA);
        GuildManager.load();
    }


    public static Database getDatabase() {
        return database;
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

    public static ImgurClient getImgurClient() {
        return imgurClient;
    }

    public static String getVersion() {
        String version = FancyBot.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = "development";
        }
        return version;
    }
}
