package com.github.nesz.fancybot.objects.database;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildCache;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Queries
{

    public static final String CREATE_TABLE_GUILD_DATA =
            "CREATE TABLE IF NOT EXISTS guildData (" +
                    "ID INT NOT NULL AUTO_INCREMENT, " +
                    "GUILD LONG NOT NULL, " +
                    "LANG VARCHAR(16) NOT NULL, " +
                    "VOLUME INT NOT NULL," +
                    "NOTIFY BOOLEAN NOT NULL, " +
                    "PREFIX VARCHAR(4) NOT NULL, " +
                    "AUTOPLAY BOOLEAN NOT NULL, " +
                    "PRIMARY KEY (ID)) CHARACTER SET utf8mb4";

    public static final String CREATE_TABLE_DATA =
            "CREATE TABLE IF NOT EXISTS data (" +
                    "ID INT NOT NULL AUTO_INCREMENT, " +
                    "UUID CHAR(36) NOT NULL, " +
                    "NAME VARCHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                    "OWNER LONG NOT NULL, " +
                    "TIME_CREATED DATETIME NOT NULL, " +
                    "PRIMARY KEY (ID)) CHARACTER SET utf8mb4";

    public static final String CREATE_TABLE_TRACKS =
            "CREATE TABLE IF NOT EXISTS tracks (" +
                    "ID INT NOT NULL AUTO_INCREMENT, " +
                    "VIDEO_ID TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                    "VIDEO_TITLE TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                    "VIDEO_UPLOADER TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, " +
                    "VIDEO_DURATION LONG, " +
                    "PLAYLIST_UUID CHAR(36) NOT NULL, " +
                    "PRIMARY KEY (ID)) CHARACTER SET utf8mb4";

    public static boolean insertGuild(final GuildCache info)
    {
        final String query = "INSERT INTO guildData VALUES(DEFAULT, ?, ?, ?, ?, ?, ?)";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setLong(1, info.getGuildId());
            preparedStatement.setString(2, info.getLanguage().name());
            preparedStatement.setInt(3, info.getVolume());
            preparedStatement.setBoolean(4, info.notifications());
            preparedStatement.setString(5, info.getPrefix());
            preparedStatement.setBoolean(5, info.isAutoPlay());
            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static boolean updateGuild(final GuildCache info)
    {
        final String query = "UPDATE guildData SET LANG = ?, VOLUME = ?, NOTIFY = ?, PREFIX = ?, AUTOPLAY = ? WHERE GUILD = ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, info.getLanguage().name());
            preparedStatement.setInt(2, info.getVolume());
            preparedStatement.setBoolean(3, info.notifications());
            preparedStatement.setString(4, info.getPrefix());
            preparedStatement.setBoolean(5, info.isAutoPlay());
            preparedStatement.setLong(6, info.getGuildId());
            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static boolean insertTrack(final AudioTrack track, final UUID uuid)
    {
        final String query = "INSERT INTO tracks VALUES(DEFAULT, ?, ?, ?, ?, ?)";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, track.getInfo().identifier);
            preparedStatement.setString(2, track.getInfo().title);
            preparedStatement.setString(3, track.getInfo().author);
            preparedStatement.setLong(4, track.getInfo().length);
            preparedStatement.setString(5, String.valueOf(uuid));

            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static List<Playlist> selectMinimizedPlaylists()
    {
        final String query = "SELECT UUID, NAME, OWNER FROM data";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {

            final ResultSet rs = preparedStatement.executeQuery();
            final List<Playlist> playlists = new ArrayList<>();
            while (rs.next())
            {
                playlists.add(new Playlist(UUID.fromString(rs.getString("UUID")), rs.getString("NAME"), rs.getLong("OWNER")));
            }
            return playlists;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    public static List<Playlist> selectMinimizedPlaylists(final String name)
    {
        final String query = "SELECT UUID, NAME, OWNER FROM data WHERE NAME LIKE ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {

            preparedStatement.setString(1, "%" + name + "%");
            final ResultSet rs = preparedStatement.executeQuery();
            final List<Playlist> playlists = new ArrayList<>();
            while (rs.next())
            {
                playlists.add(new Playlist(UUID.fromString(rs.getString("UUID")), rs.getString("NAME"), rs.getLong("OWNER")));
            }
            return playlists;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    private static Playlist buildBasicPlaylist(final UUID uuid)
    {
        final String query = "SELECT UUID, NAME, OWNER FROM data WHERE UUID = ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, String.valueOf(uuid));
            final ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new Playlist(UUID.fromString(rs.getString("UUID")), rs.getString("NAME"), rs.getLong("OWNER"));
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return null;
        }
    }

    public static Playlist buildFullPlaylist(final UUID uuid)
    {
        final String query = "SELECT VIDEO_ID, VIDEO_TITLE, VIDEO_UPLOADER, VIDEO_DURATION FROM tracks WHERE PLAYLIST_UUID = ?";
        final Playlist playlist = buildBasicPlaylist(uuid);
        if (playlist == null)
        {
            return null;
        }
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {

            preparedStatement.setString(1, String.valueOf(uuid));
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                final YoutubeAudioSourceManager yasm = PlayerManager.getAudioManager().source(YoutubeAudioSourceManager.class);
                final AudioTrack track = yasm.buildTrackObject(
                        rs.getString("VIDEO_ID"),
                        rs.getString("VIDEO_TITLE"),
                        rs.getString("VIDEO_UPLOADER"),
                        false,
                        rs.getLong("VIDEO_DURATION")
                );
                playlist.addTrack(track);
            }
            return playlist;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return null;
        }
    }

    public static boolean delete(final UUID uuid)
    {
        final String query = "DELETE FROM data WHERE UUID = ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, String.valueOf(uuid));
            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static boolean purgeTracks(final UUID uuid)
    {
        final String query = "DELETE FROM tracks WHERE PLAYLIST_UUID = ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, String.valueOf(uuid));
            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static boolean exists(final UUID uuid)
    {
        final String query = "SELECT ID FROM data WHERE UUID = ?";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, String.valueOf(uuid));
            final ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }

    public static boolean insertPlaylist(final Playlist playlist)
    {
        final String query = "INSERT INTO data VALUES(DEFAULT, ?, ?, ?, now())";
        try (final Connection connection = FancyBot.getDatabase().getDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setString(1, String.valueOf(playlist.getUUID()));
            preparedStatement.setString(2, playlist.getName());
            preparedStatement.setLong(3, playlist.getOwnerId());
            preparedStatement.executeUpdate();
            return true;
        }
        catch (final SQLException e)
        {
            FancyBot.LOGGER.error(e);
            return false;
        }
    }
}
