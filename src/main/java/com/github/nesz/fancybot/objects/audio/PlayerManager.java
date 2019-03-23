package com.github.nesz.fancybot.objects.audio;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.CollectionUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager
{

    private static final AudioPlayerManager audioManager = new DefaultAudioPlayerManager();
    private static final Map<Long, Player> PLAYERS = new ConcurrentHashMap<>();

    static
    {
        audioManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioManager.registerSourceManager(new SoundCloudAudioSourceManager());
        audioManager.registerSourceManager(new BandcampAudioSourceManager());
        audioManager.registerSourceManager(new YoutubeAudioSourceManager());
        audioManager.registerSourceManager(new VimeoAudioSourceManager());
        audioManager.registerSourceManager(new BeamAudioSourceManager());
        audioManager.registerSourceManager(new HttpAudioSourceManager());
        audioManager.getConfiguration().setFilterHotSwapEnabled(true);
        audioManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        audioManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        AudioSourceManagers.registerRemoteSources(audioManager);
        AudioSourceManagers.registerLocalSource(audioManager);
    }

    public static AudioPlayerManager getAudioManager()
    {
        return audioManager;
    }

    public static boolean isAlone(final VoiceChannel voiceChannel)
    {
        return voiceChannel.getMembers().stream().allMatch(m -> m.getUser().isBot());
    }

    public static Player getExisting(final TextChannel textChannel)
    {
        return PLAYERS.get(textChannel.getGuild().getIdLong());
    }

    public static Player getExisting(final Guild guild)
    {
        return PLAYERS.get(guild.getIdLong());
    }

    public static boolean isPlaying(final TextChannel textChannel)
    {
        return isPlaying(textChannel.getGuild());
    }

    public static boolean isPlaying(final Guild guild)
    {
        return isPlaying(guild.getIdLong());
    }

    public static boolean isPlaying(final long guildId)
    {
        return PLAYERS.containsKey(guildId);
    }

    public static Player getOrCreate(final TextChannel textChannel, final VoiceChannel voiceChannel)
    {
        return PLAYERS.computeIfAbsent(textChannel.getGuild().getIdLong(), guildId ->
                new Player(audioManager.createPlayer(), textChannel, voiceChannel));
    }

    public static boolean isInPlayingVoiceChannel(final Player player, final Member member)
    {
        final GuildVoiceState voice = member.getVoiceState();
        return voice.inVoiceChannel() && voice.getChannel() == player.getVoiceChannel();
    }


    public static void stop(final Guild guild)
    {
        final Player player = getExisting(guild);
        player.getAudioPlayer().destroy();
        guild.getAudioManager().closeAudioConnection();
        PLAYERS.remove(guild.getIdLong());
    }

    public static void play(final Player player, final AudioTrack track, final CommandContext context, final boolean playlist)
    {
        if (track == null)
        {
            return;
        }

        final AudioManager audioManager = context.guild().getAudioManager();

        if (!audioManager.isConnected())
        {
            if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect())
            {
                audioManager.openAudioConnection(context.member().getVoiceState().getChannel());
            }
        }

        player.getQueue().push(track.makeClone());
        if (!playlist)
        {
            context.respond(new MessageBuilder("{EMOTE} | {QUEUED} `{TITLE}` [{MENTION}]")
                    .with("{EMOTE}", Emote.PLAY.asEmote())
                    .with("{QUEUED}", context.translate(Messages.MUSIC_QUEUED_SONG))
                    .with("{TITLE}", track.getInfo().title)
                    .with("{MENTION}", context.member().getAsMention())
                    .build()
            );
        }
    }

    public static void loadAndPlay(final CommandContext context)
    {
        final Player player = getOrCreate(context.channel(), context.member().getVoiceState().getChannel());

        final boolean soundCloud = context.arg(0).equalsIgnoreCase("sc");
        String query = String.join(" ", context.args());

        if (!query.startsWith("http://") && !query.startsWith("https://"))
        {
            query = soundCloud ? "scsearch: " : "ytsearch: " + query;
        }


        audioManager.loadItem(query, new AudioLoadResultHandler()
        {

            @Override
            public void trackLoaded(final AudioTrack track)
            {
                play(player, track, context, false);
            }

            @Override
            public void playlistLoaded(final AudioPlaylist playlist)
            {

                if (playlist.isSearchResult())
                {
                    play(player, playlist.getTracks().get(0), context, false);
                    return;
                }

                final int able = (Constants.MAX_QUEUE_SIZE - player.getQueue().getTracks().size()) - playlist.getTracks().size();

                if (able < 0)
                {
                    context.respond(Messages.QUEUE_LIMIT_REACHED);
                    return;
                }

                final List<AudioTrack> tracks = CollectionUtils.safeSubList(playlist.getTracks(), 0, able);

                for (final AudioTrack track : tracks)
                {
                    play(player, track, context, true);
                }

                context.respond(context.translate(Messages.MUSIC_LOADED_PLAYLIST)
                        .replace("{SONGS}", String.valueOf(tracks.size()))
                        .replace("{NAME}", playlist.getName())
                );
            }

            @Override
            public void noMatches()
            {
                context.respond(Messages.MUSIC_NOTHING_FOUND);
            }

            @Override
            public void loadFailed(final FriendlyException exception)
            {
                context.respond(context.translate(Messages.MUSIC_CANNOT_LOAD) + " " + exception.getMessage());
                FancyBot.LOGGER.error("[PLAYER] Couldn't play, reason: ", exception);
            }
        });
    }

}
