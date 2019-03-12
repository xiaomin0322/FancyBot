package com.github.nesz.fancybot.objects.playlist;

import com.github.nesz.fancybot.objects.database.Queries;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.UUID;

public class PlaylistManager {

    public static boolean create(String name, Long ownerId) {
        return Queries.insertPlaylist(new Playlist(UUID.randomUUID(), name, ownerId));
    }

    public static boolean delete(UUID uuid) {
        return Queries.delete(uuid) && Queries.purgeTracks(uuid);
    }

    public static boolean addTrack(AudioTrack track, UUID uuid) {
        return Queries.insertTrack(track, uuid);
    }

}
