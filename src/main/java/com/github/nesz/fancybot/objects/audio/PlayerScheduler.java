package com.github.nesz.fancybot.objects.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class PlayerScheduler extends AudioEventAdapter {

    private final Player player;

    PlayerScheduler(Player player) {
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) {
            return;
        }

        player.getPreviousTracks().add(track.makeClone());
        switch (player.getRepeatMode()) {
            case NONE:
                player.nextTrack();
                break;
            case TRACK:
                audioPlayer.startTrack(track.makeClone(), false);
                break;
            case PLAYLIST:
                player.getQueue().add(track.makeClone());
                player.nextTrack();
                break;
        }
    }
}
