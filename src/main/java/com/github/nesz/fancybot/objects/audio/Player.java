package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.objects.guild.GuildCache;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Player
{


    private final PlayerScheduler playerScheduler;
    private final PlayerQueue playerQueue;
    private final AudioPlayer audioPlayer;
    private final AudioHandler audioHandler;
    private final GuildCache guildCache;
    private final VoiceChannel voiceChannel;
    private RepeatMode repeatMode;

    public Player(final AudioPlayer audioPlayer, final TextChannel announcingChannel, final VoiceChannel voiceChannel)
    {
        this.guildCache = GuildManager.getOrCreate(announcingChannel.getGuild());
        this.playerQueue = new PlayerQueue(this, announcingChannel);
        this.playerScheduler = new PlayerScheduler(this);
        this.audioPlayer = audioPlayer;
        this.audioPlayer.addListener(playerScheduler);
        this.audioHandler = new AudioHandler(audioPlayer);
        this.voiceChannel = voiceChannel;
        this.repeatMode = RepeatMode.NONE;
        this.audioPlayer.setVolume(guildCache.getVolume());
        this.voiceChannel.getGuild().getAudioManager().setSendingHandler(audioHandler);
    }

    public GuildCache getGuildCache()
    {
        return guildCache;
    }

    public PlayerQueue getQueue()
    {
        return playerQueue;
    }

    public PlayerScheduler getScheduler()
    {
        return playerScheduler;
    }

    public AudioHandler getAudioHandler()
    {
        return audioHandler;
    }

    public AudioPlayer getAudioPlayer()
    {
        return audioPlayer;
    }

    public RepeatMode getRepeatMode()
    {
        return repeatMode;
    }

    public void setRepeatMode(final RepeatMode repeatMode)
    {
        this.repeatMode = repeatMode;
    }

    public VoiceChannel getVoiceChannel()
    {
        return voiceChannel;
    }

}
