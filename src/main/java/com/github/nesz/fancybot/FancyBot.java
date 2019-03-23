package com.github.nesz.fancybot;

import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.config.loader.ConfigLoader;
import com.github.nesz.fancybot.http.*;
import com.github.nesz.fancybot.listeners.GuildMessageReceivedListener;
import com.github.nesz.fancybot.listeners.GuildVoiceListener;
import com.github.nesz.fancybot.listeners.ReactionListener;
import com.github.nesz.fancybot.objects.command.CommandManager;
import com.github.nesz.fancybot.objects.database.Database;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.tasks.ActivitiesTask;
import com.github.nesz.fancybot.utils.FileUtils;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class FancyBot
{

    public static final Logger LOGGER = LogManager.getLogger(FancyBot.class);

    private static Database database;
    private static HastebinClient hastebinClient;
    private static DictionaryClient dictionaryClient;
    private static YouTubeClient youTubeClient;
    private static GeniusClient geniusClient;
    private static ImgurClient imgurClient;
    private static ShardManager shardManager;

    public static void main(final String[] args)
    {
        FileUtils.saveFile("config.json");
        FileUtils.saveDir("lang", Arrays.asList("pl_PL.properties", "en_US.properties"));

        new ConfigLoader(Config.class, "config.json").override();

        database = new Database();
        hastebinClient = new HastebinClient();
        dictionaryClient = new DictionaryClient();
        youTubeClient = new YouTubeClient(Config.YOUTUBE_SECRET);
        geniusClient = new GeniusClient(Config.GENIUS_SECRET);
        imgurClient = new ImgurClient(Config.IMGUR_TOKEN);

        CommandManager.loadCommands("com.github.nesz.fancybot.commands");

        try
        {
            shardManager = new DefaultShardManagerBuilder()
                    .setToken(Config.BOT_TOKEN)
                    .setShardsTotal(-1)
                    .setAutoReconnect(true)
                    .addEventListeners(
                            new GuildMessageReceivedListener(),
                            new GuildVoiceListener(),
                            new ReactionListener()
                    ).build();
        }
        catch (final LoginException e)
        {
            LOGGER.error("Could not initialize shards!", e);
            System.exit(0);
        }

        database.executeUpdate(Queries.CREATE_TABLE_DATA);
        database.executeUpdate(Queries.CREATE_TABLE_TRACKS);
        database.executeUpdate(Queries.CREATE_TABLE_GUILD_DATA);
        GuildManager.load();

        new ActivitiesTask();
    }


    public static Database getDatabase()
    {
        return database;
    }

    public static ShardManager getShardManager()
    {
        return shardManager;
    }

    public static YouTubeClient getYouTubeClient()
    {
        return youTubeClient;
    }

    public static GeniusClient getGeniusClient()
    {
        return geniusClient;
    }

    public static ImgurClient getImgurClient()
    {
        return imgurClient;
    }

    public static DictionaryClient getDictionaryClient()
    {
        return dictionaryClient;
    }

    public static HastebinClient getHastebinClient()
    {
        return hastebinClient;
    }
}
