package com.github.nesz.fancybot.objects.guild;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.translation.Language;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class GuildManager
{

    private static final Map<Long, GuildCache> GUILD_INFOS = new ConcurrentHashMap<>();
    private static final Cache<Long, GuildCache> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .build();

    public static GuildCache getOrCreate(final Guild guild)
    {
        final GuildCache cache = CACHE.getIfPresent(guild);

        if (cache != null)
        {
            return cache;
        }

        return GUILD_INFOS.computeIfAbsent(guild.getIdLong(), v ->
        {
            final GuildCache info = new GuildCache(guild.getIdLong(), Language.ENGLISH, 100, true, ".", false);
            Queries.insertGuild(info);
            CACHE.put(guild.getIdLong(), info);
            return info;
        });

    }

    public static void remove(final Long guildID)
    {
        GUILD_INFOS.remove(guildID);
    }

    public static void load()
    {
        final String query = "SELECT * FROM guildData";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            final ResultSet rs = preparedStatement.executeQuery();

            while (rs.next())
            {
                final GuildCache guildCache = new GuildCache(
                        rs.getLong("GUILD"),
                        Language.valueOf(rs.getString("LANG")),
                        rs.getInt("VOLUME"),
                        rs.getBoolean("NOTIFY"),
                        rs.getString("PREFIX"),
                        rs.getBoolean("AUTOPLAY")
                );
                GUILD_INFOS.put(rs.getLong("GUILD"), guildCache);
            }

        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
        }
    }
}
