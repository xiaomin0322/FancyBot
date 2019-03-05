package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.CollectionUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    public static final int MAX_QUEUE_SIZE = 500;

    private static final AudioPlayerManager audioManager = new DefaultAudioPlayerManager();
    private static final Map<Long, Player> PLAYERS = new ConcurrentHashMap<>();

    static {
        audioManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    public static AudioPlayerManager getAudioManager() {
        return audioManager;
    }

    public static boolean isAlone(VoiceChannel voiceChannel) {
        return voiceChannel.getMembers().stream().anyMatch(m -> !m.getUser().isBot());
    }

    public static void stop(TextChannel textChannel) {
        Player player = getExisting(textChannel);
        player.getAudioPlayer().destroy();
        player.getGuild().getAudioManager().closeAudioConnection();
        PLAYERS.remove(textChannel.getGuild().getIdLong());
    }

    public static Player getExisting(TextChannel textChannel) {
        return PLAYERS.get(textChannel.getGuild().getIdLong());
    }

    public static Player getExisting(Guild guild) {
        return PLAYERS.get((guild.getIdLong()));
    }

    public static Player getOrCreate(TextChannel textChannel, VoiceChannel voiceChannel) {
        return PLAYERS.computeIfAbsent(textChannel.getGuild().getIdLong(), guildId -> new Player(audioManager.createPlayer(), textChannel, voiceChannel));
    }

    public static boolean isPlaying(TextChannel textChannel) {
        return isPlaying(textChannel.getGuild());
    }

    public static boolean isPlaying(Guild guild) {
        return isPlaying(guild.getIdLong());
    }

    public static boolean isPlaying(long guildId) {
        return PLAYERS.containsKey(guildId);
    }

    public static void play(Player player, AudioTrack track, Member user, TextChannel textChannel, boolean playlist) {
        AudioManager audioManager = textChannel.getGuild().getAudioManager();
        if (!textChannel.getGuild().getAudioManager().isConnected()) {
            if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
                audioManager.openAudioConnection(user.getVoiceState().getChannel());
            }
        }
        if (track == null) {
            return;
        }
        player.queue(track.makeClone());
        if (!playlist) {
            GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
            String queued = Messages.MUSIC_QUEUED_SONG.get(guildInfo.getLang());
            textChannel.sendMessage(Emote.PLAY.asEmote() + " | " + queued + " `" + track.getInfo().title + "`" + " [" + user.getAsMention() + "]").queue(
                    message -> message.delete().queueAfter(track.getInfo().length, TimeUnit.MILLISECONDS));
        }
    }

    public static void loadAndPlay(TextChannel textChannel, String trackUrl, Member user, boolean isPlaylist) {
        Player player = getOrCreate(textChannel, user.getVoiceState().getChannel());
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        audioManager.loadItemOrdered(audioManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                play(player, track, user, textChannel, isPlaylist);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                int able = (MAX_QUEUE_SIZE - player.getQueue().size()) - playlist.getTracks().size();
                if (able < 0) {
                    textChannel.sendMessage(Messages.QUEUE_LIMIT_REACHED.get(guildInfo.getLang())).queue();
                    return;
                }
                List<AudioTrack> tracks = CollectionUtils.safeSubList(playlist.getTracks(), 0, able);
                MessageEmbed eb = new EmbedBuilder()
                        .setColor(Color.BLACK)
                        .appendDescription(Messages.MUSIC_LOADED_PLAYLIST
                                .get(guildInfo.getLang())
                                .replace("{SONGS}", String.valueOf(tracks.size())))
                        .build();
                textChannel.sendMessage(eb).queue();
                for (AudioTrack track : tracks) {
                    play(player, track, user, textChannel, true);
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage(Messages.MUSIC_NOTHING_FOUND.get(guildInfo.getLang())).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage(Messages.MUSIC_CANNOT_LOAD.get(guildInfo.getLang()) + " " + exception.getMessage()).queue();
                FancyBot.LOG.error("[PLAYER] Couldn't play, reason: ", exception);
            }
        });
    }

}
