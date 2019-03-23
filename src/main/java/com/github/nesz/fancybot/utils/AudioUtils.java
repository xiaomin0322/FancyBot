package com.github.nesz.fancybot.utils;

import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioUtils
{

    public static AudioTrack buildTrack(final String id, final String title, final String channelTitle, final boolean live, final Long duration)
    {
        final YoutubeAudioSourceManager youtubeSource = PlayerManager.getAudioManager().source(YoutubeAudioSourceManager.class);
        return youtubeSource.buildTrackObject(id, title, channelTitle, live, duration);
    }

}
