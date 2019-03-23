package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.playlist.PlaylistManager;
import com.github.nesz.fancybot.objects.reactions.*;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public class PlaylistAddCommand extends Command implements Choosable<Object[]>, Interactable<Page>
{

    public PlaylistAddCommand()
    {
        super("add", CommandType.CHILD);
    }

    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("http(?:s?)://(?:www\\.)?youtu(?:be\\.com/watch\\?v=|\\.be/)([\\w\\-_]*)(&(amp;)?\u200C\u200B[\\w?\u200C\u200B=]*)?");
    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile("(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*");

    @Override
    public void execute(final CommandContext context)
    {
        if (context.args().length < 2)
        {
            context.respond("pl add playlist link/sentence");
            return;
        }

        final String[] args = StringUtils.inputWithQuotes(String.join(" ", context.args()));
        final String playlist = args[0];
        final String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        final boolean isURL = YOUTUBE_URL_PATTERN.matcher(input).matches();

        if (StringUtils.isUUID(playlist))
        {

            final UUID uuid = UUID.fromString(playlist);

            if (!Queries.exists(uuid))
            {
                context.respond("Playlist with that ID doesn't exists");
                return;
            }

            final AudioTrack track = isURL ? trackFromID(getYouTubeIdFromURL(input)) : trackFromQuery(input);

            if (track == null)
            {
                context.respond("Track does not exists");
                return;
            }

            if (track.getInfo().isStream)
            {
                context.respond("Can't add stream to playlist");
                return;
            }

            final boolean success = PlaylistManager.addTrack(track, uuid);

            if (success)
            {
                context.respond("Inserted track to playlist");
                return;
            }

            context.respond("Error occurred while inserting track");
            return;
        }

        final List<Playlist> playlists = Queries.selectMinimizedPlaylists(playlist);

        if (playlists.isEmpty())
        {
            context.respond("Playlist with that name doesn't exists");
            return;
        }

        final AudioTrack track = isURL ? trackFromID(getYouTubeIdFromURL(input)) : trackFromQuery(input);

        if (track == null)
        {
            context.respond("Track does not exists");
            return;
        }

        if (track.getInfo().isStream)
        {
            context.respond("Can't add stream to playlist");
            return;
        }

        if (playlists.size() == 1)
        {

            final boolean success = PlaylistManager.addTrack(track, playlists.get(0).getUUID());

            if (success)
            {
                context.respond("Inserted track to playlist");
                return;
            }

            context.respond("Error occurred while inserting track");
            return;
        }

        final Object[] extra = new Object[]{ playlists, track, context };
        final int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);

        context.respond(printQueue(context, playlists, 1), msg ->
        {
            if (maxPage > 1) ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, extra)));

            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });

        ChoosableManager.registerListener(context.author().getIdLong(), getChoosableListener(extra));

    }


    @Override
    public Consumer<GuildMessageReceivedEvent> getChoosableListener(final Object[] data)
    {
        return event ->
        {
            final String message = event.getMessage().getContentRaw();

            if (message.equalsIgnoreCase("cancel"))
            {
                ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());
                return;
            }

            if (!StringUtils.isNumeric(message))
            {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            final List<Playlist> playlists = (List<Playlist>) data[0];
            final int option = Integer.valueOf(message) - 1;

            if (playlists.size() < option || option > playlists.size())
            {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());

            final AudioTrack track = (AudioTrack) data[1];
            final Playlist playlist = playlists.get(option);
            final boolean success = PlaylistManager.addTrack(track, playlist.getUUID());

            if (success)
            {
                event.getChannel().sendMessage("Inserted track to playlist").queue();
                return;
            }

            event.getChannel().sendMessage("Error occurred while inserting track").queue();
        };
    }

    @Override
    public Reaction<Page> getReactionListener(final Page initialData)
    {
        final Reaction<Page> listener = new Reaction<>(initialData);
        final Object[] extra = (Object[]) initialData.getExtra();
        final List<Playlist> playlists = (List<Playlist>) extra[0];
        final CommandContext context = (CommandContext) extra[2];

        listener.registerReaction(Emote.PREV, o ->
        {
            if (listener.getData().previousPage())
            {
                o.editMessage(printQueue(context, playlists, listener.getData().getCurrentPage())).complete();
            }
        });

        listener.registerReaction(Emote.NEXT, o ->
        {
            if (listener.getData().nextPage())
            {
                o.editMessage(printQueue(context, playlists, listener.getData().getCurrentPage())).complete();
            }
        });

        return listener;
    }

    private static final int ITEMS_PER_PAGE = 20;

    private MessageEmbed printQueue(final CommandContext context, final List<Playlist> playlists, final int page)
    {
        final EmbedBuilder eb = EmbedHelper.basicEmbed(Color.RED, context)
                .setAuthor(context.translate(Messages.QUEUE_FOR_SERVER)
                .replace("{SERVER}", context.guild().getName()), null, context.guild().getIconUrl());

        final int start = Math.max(0, (page - 1) * ITEMS_PER_PAGE);
        final int end = Math.min(playlists.size() - 1, start + ITEMS_PER_PAGE);

        for (int i = start; i <= end; i++)
        {
            //ID. NAME (OWNER)
            final Playlist playlist = playlists.get(i);
            final String name = FancyBot.getShardManager().getUserById(playlist.getOwnerId()).getName();
            eb.appendDescription(String.format("%s. **%s (%s)**\n", i + 1, playlist.getName(), name));
        }

        eb.appendDescription("\nChoice will be available for 1 minute \n you can also type 'cancel' to cancel");

        return eb.build();
    }

    private String getYouTubeIdFromURL(final String youTubeUrl)
    {
        final Matcher matcher = YOUTUBE_ID_PATTERN.matcher(youTubeUrl);

        if (matcher.find())
        {
            return matcher.group();
        }

        return null;
    }

    private AudioTrack trackFromQuery(final String query)
    {
        final AtomicReference<AudioTrack> audioTrack = new AtomicReference<>();
        final Future<Void> future = PlayerManager.getAudioManager().loadItem("ytsearch: " + query, new FunctionalResultHandler(
                audioTrack::set, plz -> audioTrack.set(plz.getTracks().get(0)), null, null)
        );

        while (!future.isDone())
        {
        }
        // ^^ IM FULLY AWARE OF THAT GONNA REWRITE IT ONE DAY : ) THOSE PLAYLISTS ARE JUST TEMPORARY

        return audioTrack.get();
    }

    private AudioTrack trackFromID(final String id)
    {
        AudioTrack ac = null;
        final HTTPResponse<JSONObject> video = FancyBot.getYouTubeClient().retrieveVideo(id);

        if (!video.getData().isPresent())
        {
            return null;
        }

        try
        {
            final JSONObject data = video.getData().get();
            final YoutubeAudioSourceManager yasm = PlayerManager.getAudioManager().source(YoutubeAudioSourceManager.class);
            ac = yasm.buildTrackObject(
                    data.getString("id"),
                    data.getJSONObject("snippet").getString("title"),
                    data.getJSONObject("snippet").getString("channelTitle"),
                    data.getJSONObject("snippet").getString("liveBroadcastContent").contains("live"),
                    FancyBot.getYouTubeClient().toLongDuration(data.getJSONObject("contentDetails").getString("duration"))
            );
        }
        catch (final Exception e)
        {
            FancyBot.LOGGER.error("Error occurred while building track", e);
        }

        return ac;
    }
}
