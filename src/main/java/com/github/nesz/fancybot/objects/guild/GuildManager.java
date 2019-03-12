package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.translation.Lang;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildManager {

    private static final Map<Long, GuildInfo> GUILD_INFOS = new ConcurrentHashMap<>();

    public static GuildInfo getOrCreate(Guild guild) {
        return GUILD_INFOS.computeIfAbsent(guild.getIdLong(), v -> {
            GuildInfo info = new GuildInfo(guild.getIdLong(), Lang.ENGLISH, 100, true);
            Queries.insertGuild(info);
            return info;
        });
    }

    public static void remove(Long guildID) {
        GUILD_INFOS.remove(guildID);
    }

    public static void load() {
        String query = "SELECT * FROM guildData";
        try (Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                GuildInfo guildInfo = new GuildInfo(rs.getLong("GUILD"), Lang.valueOf(rs.getString("LANG")), rs.getInt("VOLUME"), rs.getBoolean("NOTIFY"));
                GUILD_INFOS.put(rs.getLong("GUILD"), guildInfo);
            }
        }
        catch (SQLException e) {
            FancyBot.LOG.error(e);
        }
    }
}
