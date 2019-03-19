package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Player {

    private final LinkedList<AudioTrack> queue;
    private final LinkedList<AudioTrack> previousTracks;
    private final PlayerScheduler playerScheduler;
    private final TextChannel triggerChannel;
    private final VoiceChannel voiceChannel;
    private final AudioPlayer audioPlayer;
    private final AudioHandler audioHandler;
    private final GuildInfo guildInfo;
    private RepeatMode repeatMode;

    public Player(AudioPlayer audioPlayer, TextChannel triggerChannel, VoiceChannel voiceChannel) {
        this.guildInfo = GuildManager.getOrCreate(triggerChannel.getGuild());
        this.audioPlayer = audioPlayer;
        this.playerScheduler = new PlayerScheduler(this);
        this.audioPlayer.addListener(playerScheduler);
        this.audioHandler = new AudioHandler(audioPlayer);
        this.triggerChannel = triggerChannel;
        this.voiceChannel   = voiceChannel;
        this.queue = new LinkedList<>();
        this.previousTracks = new LinkedList<>();
        this.repeatMode = RepeatMode.NONE;
        getGuild().getAudioManager().setSendingHandler(audioHandler);
        audioPlayer.setVolume(guildInfo.getVolume());
    }

    public LinkedList<AudioTrack> getQueue() {
        return queue;
    }

    public List<AudioTrack> getPreviousTracks() {
        return previousTracks;
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

    public PlayerScheduler getPlayerScheduler() {
        return playerScheduler;
    }

    public GuildInfo getGuildInfo() {
        return guildInfo;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
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

    public void shuffleQueue() {
        Collections.shuffle(queue);
    }

    private void messageNotify() {
        if (!guildInfo.notifications()) {
            return;
        }
        AudioTrackInfo info = audioPlayer.getPlayingTrack().getInfo();
        String translated = Messages.MUSIC_NOW_PLAYING.get(guildInfo.getLang());

        MessagingHelper.sendAsync(triggerChannel, Emote.PLAY.asEmote() + " | " + translated + " `" + info.title + "`" + " `" + StringUtils.getDurationMinutes(info.length) + "`");
    }

    public void previousTrack() {
        audioPlayer.startTrack(previousTracks.pollLast(), false);
        messageNotify();
    }

    public void skip(int howMany) {
        if (queue.size() < howMany) {
            if (audioPlayer.getPlayingTrack() != null) {
                audioPlayer.stopTrack();
            }
            if (guildInfo.isAutoPlay()) {
                AudioTrack previousTrack = previousTracks.pollLast();
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

        for (int i = 1; i < howMany; i++) {
            queue.poll();
        }

        nextTrack();
    }

    public void nextTrack() {

        if (queue.isEmpty()) {
            if (audioPlayer.getPlayingTrack() != null) {
                audioPlayer.stopTrack();
            }
            if (guildInfo.isAutoPlay()) {
                AudioTrack previousTrack = previousTracks.pollLast();
                if (previousTrack == null) {
                    return;
                }
                AudioTrack related = FancyBot.getYouTubeClient().getRelatedVideo(previousTrack.getIdentifier());
                if (related == null) {
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

}
