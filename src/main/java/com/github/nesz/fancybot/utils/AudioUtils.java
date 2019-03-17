package com.github.nesz.fancybot.utils;

import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioUtils {

    public static AudioTrack buildTrack(String id, String title, String channelTitle, boolean live, Long duration) {
        YoutubeAudioSourceManager youtubeSource = PlayerManager.getAudioManager().source(YoutubeAudioSourceManager.class);
        return youtubeSource.buildTrackObject(id, title, channelTitle, live, duration);
    }

}
