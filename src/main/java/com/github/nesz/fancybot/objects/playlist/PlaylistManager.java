package com.github.nesz.fancybot.objects.playlist;

import com.github.nesz.fancybot.objects.database.Queries;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.UUID;

public class PlaylistManager
{

    public static boolean create(final String name, final Long ownerId)
    {
        return Queries.insertPlaylist(new Playlist(UUID.randomUUID(), name, ownerId));
    }

    public static boolean delete(final UUID uuid)
    {
        return Queries.delete(uuid) && Queries.purgeTracks(uuid);
    }

    public static boolean addTrack(final AudioTrack track, final UUID uuid)
    {
        return Queries.insertTrack(track, uuid);
    }

}
