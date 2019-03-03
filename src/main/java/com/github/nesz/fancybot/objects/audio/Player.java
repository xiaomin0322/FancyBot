package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.google.common.collect.EvictingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.awt.*;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Player extends AudioEventAdapter {

    private final LinkedBlockingQueue<AudioTrack> queue;
    private final Queue<AudioTrack> previous;
    private final TextChannel triggerChannel;
    private final VoiceChannel voiceChannel;
    private final AudioPlayer audioPlayer;
    private final AudioHandler audioHandler;
    private boolean notifications;
    private RepeatMode repeatMode;
    private int volume;

    public Player(AudioPlayer audioPlayer, TextChannel triggerChannel, VoiceChannel voiceChannel) {
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(this);
        this.audioHandler = new AudioHandler(audioPlayer);
        this.triggerChannel = triggerChannel;
        this.voiceChannel   = voiceChannel;
        this.queue = new LinkedBlockingQueue<>();
        this.previous = EvictingQueue.create(10);
        this.notifications = true;
        this.repeatMode = RepeatMode.NONE;
        this.volume = 100;
        getGuild().getAudioManager().setSendingHandler(audioHandler);
    }

    public LinkedBlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public Queue<AudioTrack> getPrevious() {
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
        GuildInfo guildInfo = GuildManager.getOrCreate(getGuild().getIdLong());
        String translated = Messages.MUSIC_NOW_PLAYING.get(guildInfo.getLang());
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.BLACK)
                .setDescription(translated + " [" + info.title + "](" + info.uri + ")");

        triggerChannel.sendMessage(eb.build()).queue(message ->
                message.delete().queueAfter(info.length, TimeUnit.MILLISECONDS));
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
