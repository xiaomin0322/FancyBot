package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.LimitedList;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.RandomUtil;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PlayerQueue
{

    private final Player player;
    private final LimitedList<AudioTrack> previousTracks;
    private final LinkedList<AudioTrack> queue;
    private final TextChannel announcingChannel;

    PlayerQueue(final Player player, final TextChannel announcingChannel)
    {
        this.announcingChannel = announcingChannel;
        this.previousTracks = new LimitedList<>(20);
        this.queue = new LinkedList<>();
        this.player = player;
    }

    public int size()
    {
        return queue.size();
    }

    public List<AudioTrack> getTracks()
    {
        return queue;
    }

    public TextChannel getAnnouncingChannel()
    {
        return announcingChannel;
    }

    public LimitedList<AudioTrack> getPreviousTracks()
    {
        return previousTracks;
    }

    public void shuffle()
    {
        Collections.shuffle(queue);
    }

    public void pushPreviousTrack(final AudioTrack previousTrack)
    {
        this.previousTracks.push(previousTrack);
    }

    public void push(final AudioTrack track)
    {
        if (!player.getAudioPlayer().startTrack(track, true))
        {
            queue.offer(track);
        }
    }

    public void playPrevious()
    {
        player.getAudioPlayer().startTrack(previousTracks.pullLast(), false);
        announce();
    }

    private boolean hasOccurredPreviously(final AudioTrack track) {
        return previousTracks.stream()
                .anyMatch(v -> v.getIdentifier().equalsIgnoreCase(track.getIdentifier()));
    }

    public void playNext()
    {
        if (queue.isEmpty())
        {
            if (player.getAudioPlayer().getPlayingTrack() != null)
            {
                player.getAudioPlayer().stopTrack();
            }

            if (player.getGuildCache().isAutoPlay())
            {
                handleAutoplay();
            }

            return;
        }

        player.getAudioPlayer().startTrack(queue.poll(), false);
        announce();
    }

    public void skip(final int howMany)
    {
        if (queue.size() < howMany)
        {
            if (player.getAudioPlayer().getPlayingTrack() != null)
            {
                player.getAudioPlayer().stopTrack();
            }
            if (player.getGuildCache().isAutoPlay())
            {
                handleAutoplay();
            }
            return;
        }

        for (int i = 1; i < howMany; i++)
        {
            queue.poll();
        }

        playNext();
    }

    private void announce()
    {
        if (!player.getGuildCache().notifications())
        {
            return;
        }

        final AudioTrackInfo info = player.getAudioPlayer().getPlayingTrack().getInfo();
        final String message = new MessageBuilder("{EMOTE} | {PLAYING} `{TITLE}` `{TIME}`")
                        .with("{EMOTE}", Emote.PLAY.asEmote())
                        .with("{PLAYING}", Messages.MUSIC_NOW_PLAYING.get(player.getGuildCache().getLanguage()))
                        .with("{TITLE}", info.title)
                        .with("{TIME}", StringUtils.getDurationMinutes(info.length))
                        .build();

        MessagingHelper.sendAsync(announcingChannel, message);
    }

    private void handleAutoplay()
    {
        final AudioTrack previousTrack = getPreviousTracks().getLast();

        if (previousTrack == null)
        {
            return;
        }

        final HTTPResponse<List<AudioTrack>> response = FancyBot.getYouTubeClient()
                .retrieveRelatedVideos(previousTrack.getIdentifier());

        if (!response.getData().isPresent())
        {
            MessagingHelper.sendAsync(announcingChannel, "No related videos has been found");
            return;
        }

        final List<AudioTrack> related = response.getData().get();

        if (related.isEmpty())
        {
            MessagingHelper.sendAsync(announcingChannel, "No related videos has been found");
            return;
        }

        Optional<AudioTrack> choosen = related.stream()
                .filter(s -> !hasOccurredPreviously(s))
                .filter(s -> Constants.IGNORED_TRACKS.getIfPresent(s.getIdentifier()) == null)
                .findFirst();

        if (!choosen.isPresent())
        {
            choosen = Optional.of(related.get(RandomUtil.getRandomIntBetween(0, related.size() - 1)));
        }

        player.getAudioPlayer().startTrack(choosen.get(), false);
        announce();
    }

}
