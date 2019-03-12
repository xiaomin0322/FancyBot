package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Player extends AudioEventAdapter {

    private final LinkedBlockingQueue<AudioTrack> queue;
    private final LinkedBlockingQueue<AudioTrack> previous;
    private final TextChannel triggerChannel;
    private final VoiceChannel voiceChannel;
    private final AudioPlayer audioPlayer;
    private final AudioHandler audioHandler;
    private boolean notifications;
    private RepeatMode repeatMode;
    private int volume;

    public Player(AudioPlayer audioPlayer, TextChannel triggerChannel, VoiceChannel voiceChannel) {
        GuildInfo guildInfo = GuildManager.getOrCreate(triggerChannel.getGuild());
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.audioHandler = new AudioHandler(audioPlayer);
        this.triggerChannel = triggerChannel;
        this.voiceChannel   = voiceChannel;
        this.queue = new LinkedBlockingQueue<>();
        this.previous = new LinkedBlockingQueue<>();
        this.notifications = guildInfo.notifications();
        this.repeatMode = RepeatMode.NONE;
        this.volume = guildInfo.getVolume();
        getGuild().getAudioManager().setSendingHandler(audioHandler);
    }

    public LinkedBlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public LinkedBlockingQueue<AudioTrack> getPrevious() {
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

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
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
        GuildInfo guildInfo = GuildManager.getOrCreate(getGuild());
        String translated = Messages.MUSIC_NOW_PLAYING.get(guildInfo.getLang());
        triggerChannel.sendMessage(Emote.PLAY.asEmote() + " | " + translated + " `" + info.title + "`" + " `" + StringUtils.getDurationMinutes(info.length) + "`").queue(
                        message -> message.delete().queueAfter(info.length, TimeUnit.MILLISECONDS));
    }

    public void previousTrack() {
        audioPlayer.startTrack(previous.poll(), false);
        messageNotify();
    }

    public void nextTrack() {

        if (queue.isEmpty()) {
            if (audioPlayer.getPlayingTrack() != null) {
                audioPlayer.stopTrack();
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

        previous.offer(track.makeClone());
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
