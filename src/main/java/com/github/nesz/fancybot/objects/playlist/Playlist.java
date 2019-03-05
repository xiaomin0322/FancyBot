package com.github.nesz.fancybot.objects.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Playlist {

    private final UUID uuid;
    private String name;
    private Long ownerId;
    private final Set<AudioTrack> tracks;

    public Playlist(UUID uuid, String name, Long ownerId) {
        this.uuid = uuid;
        this.name = name;
        this.ownerId = ownerId;
        this.tracks = new LinkedHashSet<>();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Set<AudioTrack> getTracks() {
        return tracks;
    }

    public void addTrack(AudioTrack track) {
        tracks.add(track);
    }

    public void removeTrack(AudioTrack track) {
        tracks.remove(track);
    }

    public long getLength() {
        return tracks.stream().mapToLong(value -> value.getInfo().length).sum();
    }
}