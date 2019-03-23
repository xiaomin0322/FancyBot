package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class PlayerScheduler extends AudioEventAdapter
{

    private final Player player;

    PlayerScheduler(final Player player)
    {
        this.player = player;
    }

    @Override
    public void onTrackEnd(final AudioPlayer audioPlayer, final AudioTrack track, final AudioTrackEndReason endReason)
    {
        player.getQueue().pushPreviousTrack(track.makeClone());

        if (!endReason.mayStartNext)
        {
            return;
        }

        switch (player.getRepeatMode())
        {
            case NONE:
                player.getQueue().playNext();
                break;
            case TRACK:
                audioPlayer.startTrack(track.makeClone(), false);
                break;
            case PLAYLIST:
                player.getQueue().push(track.makeClone());
                player.getQueue().playNext();
                break;
        }
    }

    @Override
    public void onTrackException(final AudioPlayer player, final AudioTrack track, final FriendlyException exception)
    {
        Constants.IGNORED_TRACKS.put(track.getIdentifier(), "");
        MessagingHelper.sendAsync(this.player.getQueue().getAnnouncingChannel(), exception.getMessage());
    }
}
