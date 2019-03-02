package com.github.nesz.fancybot.objects.audio;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Player extends AudioEventAdapter {

    private final LinkedBlockingQueue<AudioTrack> queue;
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
        this.notifications = true;
        this.repeatMode = RepeatMode.NONE;
        this.volume = 100;
        getGuild().getAudioManager().setSendingHandler(audioHandler);
    }

    public LinkedBlockingQueue<AudioTrack> getQueue() {
        return queue;
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

    public void nextTrack() {
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.BLACK);

        if (queue.isEmpty()) {
            return;
        }

        audioPlayer.startTrack(queue.poll(), false);
        if (notifications) {
            AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
            eb.setDescription("Now playing [" + info.title + "](" + info.uri + ")");
            triggerChannel.sendMessage(eb.build()).queue(message ->
                    message.delete().queueAfter(info.length, TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) {
            return;
        }

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
