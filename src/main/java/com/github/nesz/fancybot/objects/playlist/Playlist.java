package com.github.nesz.fancybot.objects.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Playlist
{

    private final UUID uuid;
    private String name;
    private Long ownerId;
    private final Set<AudioTrack> tracks;

    public Playlist(final UUID uuid, final String name, final Long ownerId)
    {
        this.uuid = uuid;
        this.name = name;
        this.ownerId = ownerId;
        this.tracks = new LinkedHashSet<>();
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Long getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(final Long ownerId)
    {
        this.ownerId = ownerId;
    }

    public Set<AudioTrack> getTracks()
    {
        return tracks;
    }

    public void addTrack(final AudioTrack track)
    {
        tracks.add(track);
    }

    public void removeTrack(final AudioTrack track)
    {
        tracks.remove(track);
    }

    public long getLength()
    {
        return tracks.stream().mapToLong(value -> value.getInfo().length).sum();
    }
}