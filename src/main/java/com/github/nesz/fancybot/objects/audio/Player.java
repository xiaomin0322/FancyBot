package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Player extends AudioEventAdapter {

    private LinkedBlockingQueue<AudioTrack> queue;
    private final List<AudioTrack> previous;
    private final GuildInfo guildInfo;
    private final TextChannel triggerChannel;
    private final VoiceChannel voiceChannel;
    private final AudioPlayer audioPlayer;
    private final AudioHandler audioHandler;
    private boolean notifications;
    private RepeatMode repeatMode;

    public Player(AudioPlayer audioPlayer, TextChannel triggerChannel, VoiceChannel voiceChannel) {
        this.guildInfo = GuildManager.getOrCreate(triggerChannel.getGuild());
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.audioHandler = new AudioHandler(audioPlayer);
        this.triggerChannel = triggerChannel;
        this.voiceChannel   = voiceChannel;
        this.queue = new LinkedBlockingQueue<>();
        this.previous = new ArrayList<>();
        this.notifications = guildInfo.notifications();
        this.repeatMode = RepeatMode.NONE;
        getGuild().getAudioManager().setSendingHandler(audioHandler);
        audioPlayer.setVolume(guildInfo.getVolume());
    }

    public LinkedBlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public List<AudioTrack> getPrevious() {
        return previous;
    }

    public TextChannel getTriggerChannel() {
        return triggerChannel;
    }

    public Guild getGuild() {
        return triggerChannel.getGuild();
    }

    public AudioHandler getAudioHandler() {
        return audioHandler;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public boolean notifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }

    public void queue(AudioTrack track) {
        if (!audioPlayer.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    private void messageNotify() {
        if (!notifications) {
            return;
        }
        AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
        String translated = Messages.MUSIC_NOW_PLAYING.get(guildInfo.getLang());

        MessagingHelper.sendAsync(triggerChannel, Emote.PLAY.asEmote() + " | " + translated + " `" + info.title + "`" + " `" + StringUtils.getDurationMinutes(info.length) + "`");
    }

    private AudioTrack getPreviousTrack() {
        return previous.get(previous.size() - 1);
    }

    public void previousTrack() {
        audioPlayer.startTrack(getPreviousTrack(), false);
        previous.remove(getPreviousTrack());
        messageNotify();
    }

    public void skip(int howMany) {
        if (queue.size() < howMany) {
            if (audioPlayer.getPlayingTrack() != null) {
                audioPlayer.stopTrack();
            }
            if (guildInfo.isAutoPlay()) {
                AudioTrack previousTrack = getPreviousTrack();
                if (previousTrack == null) {
                    return;
                }
                AudioTrack related = FancyBot.getYouTubeClient().getRelatedVideo(previousTrack.getIdentifier());
                queue.add(related);
                audioPlayer.startTrack(queue.poll(), false);
                messageNotify();
            }
            return;
        }
        queue = queue.stream().skip(howMany).collect(Collectors.toCollection(LinkedBlockingQueue::new));

        nextTrack();
    }

    public void nextTrack() {

        if (queue.isEmpty()) {
            if (audioPlayer.getPlayingTrack() != null) {
                audioPlayer.stopTrack();
            }
            FancyBot.LOG.debug("EMPTY");
            if (guildInfo.isAutoPlay()) {
                FancyBot.LOG.debug("AUTOPLAY");
                AudioTrack previousTrack = getPreviousTrack();
                if (previousTrack == null) {
                    return;
                }
                FancyBot.LOG.debug("previousTrack");
                AudioTrack related = FancyBot.getYouTubeClient().getRelatedVideo(previousTrack.getIdentifier());
                if (related == null) {
                    FancyBot.LOG.debug("related == null");
                    return;
                }
                queue.add(related);
                audioPlayer.startTrack(queue.poll(), false);
                messageNotify();
            }
            return;
        }

        audioPlayer.startTrack(queue.poll(), false);
        messageNotify();
    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) {
            return;
        }

        previous.add(track.makeClone());
        switch (repeatMode) {
            case NONE:
                nextTrack();
                break;
            case TRACK:
                audioPlayer.startTrack(track.makeClone(), false);
                break;
            case PLAYLIST:
                queue.add(track.makeClone());
                nextTrack();
                break;
        }
    }

}
