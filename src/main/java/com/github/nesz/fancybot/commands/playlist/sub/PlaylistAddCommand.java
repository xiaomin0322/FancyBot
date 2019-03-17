package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.playlist.PlaylistManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.*;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaylistAddCommand extends AbstractCommand implements Choosable<Object[]>, Interactable<Page> {

    public PlaylistAddCommand() {
        super("add", Collections.emptyList(), Collections.emptyList(), CommandType.SUB);
    }

    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("http(?:s?)://(?:www\\.)?youtu(?:be\\.com/watch\\?v=|\\.be/)([\\w\\-_]*)(&(amp;)?\u200C\u200B[\\w?\u200C\u200B=]*)?");
    private static final Pattern YOUTUBE_ID_PATTERN = Pattern.compile("(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*");

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length < 2) {
            textChannel.sendMessage("pl add playlist link/sentence").queue();
            return;
        }

        args = StringUtils.inputWithQuotes(String.join(" ", args));
        String playlist = args[0];
        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        boolean isURL = YOUTUBE_URL_PATTERN.matcher(input).matches();

        if (StringUtils.isUUID(playlist)) {
            UUID uuid = UUID.fromString(playlist);
            if (!Queries.exists(uuid)) {
                textChannel.sendMessage("Playlist with that ID doesn't exists").queue();
                return;
            }
            AudioTrack track = trackFromID(isURL ? getYouTubeIdFromURL(input) : getYoutubeIdFromInput(input));
            if (track == null) {
                textChannel.sendMessage("Track does not exists").queue();
                return;
            }
            if (track.getInfo().isStream) {
                textChannel.sendMessage("Can't add stream to playlist").queue();
                return;
            }
            boolean success = PlaylistManager.addTrack(track, uuid);
            if (success) {
                textChannel.sendMessage("Inserted track to playlist").queue();
                return;
            }
            textChannel.sendMessage("Error occurred while inserting track").queue();
            return;
        }

        List<Playlist> playlists = Queries.selectMinimizedPlaylists(playlist);
        if (playlists.isEmpty()) {
            textChannel.sendMessage("Playlist with that name doesn't exists").queue();
            return;
        }

        AudioTrack track = trackFromID(isURL ? getYouTubeIdFromURL(input) : getYoutubeIdFromInput(input));
        if (track == null) {
            textChannel.sendMessage("Track does not exists").queue();
            return;
        }

        if (track.getInfo().isStream) {
            textChannel.sendMessage("Can't add stream to playlist").queue();
            return;
        }

        if (playlists.size() == 1) {
            boolean success = PlaylistManager.addTrack(track, playlists.get(0).getUUID());
            if (success) {
                textChannel.sendMessage("Inserted track to playlist").queue();
                return;
            }
            textChannel.sendMessage("Error occurred while inserting track").queue();
            return;
        }

        Object[] extra = new Object[] {playlists, track};
        int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);
        textChannel.sendMessage(printQueue(textChannel, EmbedHelper.getFooter(member), playlists, 1)).queue(msg -> {
            if (maxPage > 1) {
                ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, textChannel, extra)));
            }
            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });
        ChoosableManager.registerListener(member.getUser().getIdLong(), getChoosableListener(extra));

    }


    @Override
    public Consumer<GuildMessageReceivedEvent> getChoosableListener(Object[] data) {
        return event -> {
            String message = event.getMessage().getContentRaw();
            if (message.equalsIgnoreCase("cancel")) {
                ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());
                return;
            }

            if (!StringUtils.isNumeric(message)) {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            List<Playlist> playlists = (List<Playlist>) data[0];
            int option = Integer.valueOf(message) - 1;
            if (playlists.size() < option || option > playlists.size()) {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());

            AudioTrack track = (AudioTrack) data[1];
            Playlist playlist = playlists.get(option);
            boolean success = PlaylistManager.addTrack(track, playlist.getUUID());
            if (success) {
                event.getChannel().sendMessage("Inserted track to playlist").queue();
                return;
            }
            event.getChannel().sendMessage("Error occurred while inserting track").queue();
        };
    }

    @Override
    public Reaction<Page> getReactionListener(Page initialData) {
        Reaction<Page> listener = new Reaction<>(initialData);
        Object[] extra = (Object[]) initialData.getExtra();
        List<Playlist> playlists = (List<Playlist>) extra[0];
        listener.registerReaction(Emote.PREV, o -> {
            if (listener.getData().previousPage()) {
                o.editMessage(printQueue(
                        initialData.getTextChannel(),
                        o.getEmbeds().get(0).getFooter(),
                        playlists,
                        listener.getData().getCurrentPage()))
                        .complete();
            }
        });
        listener.registerReaction(Emote.NEXT, o -> {
            if (listener.getData().nextPage()) {
                o.editMessage(printQueue(
                        initialData.getTextChannel(),
                        o.getEmbeds().get(0).getFooter(),
                        playlists,
                        listener.getData().getCurrentPage()))
                        .complete();
            }
        });
        return listener;
    }

    private static final int ITEMS_PER_PAGE = 20;

    private MessageEmbed printQueue(TextChannel textChannel, MessageEmbed.Footer footer, List<Playlist> playlists, int page) {
        Guild guild = textChannel.getGuild();

        Lang lang = GuildManager.getOrCreate(guild).getLang();

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(footer.getText(), footer.getIconUrl())
                .setAuthor(Messages.QUEUE_FOR_SERVER.get(lang).replace("{SERVER}", guild.getName()), null, guild.getIconUrl());

        int start = Math.max(0, (page - 1) * ITEMS_PER_PAGE);
        int end   = Math.min(playlists.size() - 1, start + ITEMS_PER_PAGE);
        for (int i = start; i <= end; i++) {
            //ID. NAME (OWNER)
            Playlist playlist = playlists.get(i);
            String name = FancyBot.getShardManager().getUserById(playlist.getOwnerId()).getName();
            eb.appendDescription(i+1 + ". " +
                    EmbedHelper.bold(playlist.getName() + " (" + name + ") \n"));
        }

        eb.appendDescription("\nChoice will bi available for 1 minute \n you can also type 'cancel' to cancel");

        return eb.build();
    }

    private String getYouTubeIdFromURL(String youTubeUrl) {
        Matcher matcher = YOUTUBE_ID_PATTERN.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private String getYoutubeIdFromInput(String query) {
        return FancyBot.getYouTubeClient().getFirstID(query);
    }

    private AudioTrack trackFromID(String id) {
        AudioTrack ac = null;
        JSONObject video = FancyBot.getYouTubeClient().getVideo(id);
        if (video == null) {
            return null;
        }
        try {
            YoutubeAudioSourceManager yasm = PlayerManager.getAudioManager().source(YoutubeAudioSourceManager.class);
            ac = yasm.buildTrackObject(
                    video.getString("id"),
                    video.getJSONObject("snippet").getString("title"),
                    video.getJSONObject("snippet").getString("channelTitle"),
                    video.getJSONObject("snippet").getString("liveBroadcastContent").contains("live"),
                    FancyBot.getYouTubeClient().toLongDuration(video.getJSONObject("contentDetails").getString("duration"))
            );
        }
        catch (Exception e) {
            FancyBot.LOG.error("Error occurred while building track", e);
        }

        return ac;
    }
}
