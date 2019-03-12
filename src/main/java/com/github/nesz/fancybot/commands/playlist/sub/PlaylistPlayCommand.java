package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.*;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PlaylistPlayCommand extends AbstractCommand implements Choosable<List<Playlist>>, Interactable<Page> {

    public PlaylistPlayCommand() {
        super("play", new HashSet<>(Arrays.asList("load")), Collections.emptySet(), CommandType.SUB);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();

        if (!member.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_VOICE_CHANNEL.get(lang)).queue();
            return;
        }

        if (PlayerManager.isPlaying(textChannel)) {
            Player player = PlayerManager.getExisting(textChannel);
            if (member.getVoiceState().getChannel() != player.getVoiceChannel()) {
                textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang)).queue();
                return;
            }
        }

        if (args.length == 1 && StringUtils.isUUID(args[0])) {
            if (!Queries.exists(UUID.fromString(args[0]))) {
                textChannel.sendMessage("Playlist with that ID doesn't exists").queue();
                return;
            }
            Playlist playlist = Queries.buildFullPlaylist(UUID.fromString(args[0]));
            if (playlist == null) {
                textChannel.sendMessage("Error occurred while accessing playlist").queue();
                return;
            }
            play(textChannel, member, playlist);
            return;
        }

        String name = String.join(" ", args);
        if (name.length() > Config.PLAYLIST_NAME_LENGTH) {
            textChannel.sendMessage("Playlist name cannot be longer than 32 characters").queue();
            return;
        }

        List<Playlist> playlists = Queries.selectMinimizedPlaylists(name);
        if (playlists.isEmpty()) {
            textChannel.sendMessage("Playlist with that name doesn't exists").queue();
            return;
        }

        if (playlists.size() == 1) {
            Playlist playlist = Queries.buildFullPlaylist(playlists.get(0).getUUID());
            if (playlist == null) {
                textChannel.sendMessage("Error occurred while accessing playlist").queue();
                return;
            }
            play(textChannel, member, playlist);
            return;
        }

        int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);
        textChannel.sendMessage(printQueue(textChannel, EmbedHelper.getFooter(member), playlists, 1)).queue(msg -> {
            if (maxPage > 1) {
                ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, textChannel, playlists)));
            }
            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });
        ChoosableManager.registerListener(member.getUser().getIdLong(), getChoosableListener(playlists));
    }

    @Override
    public Consumer<GuildMessageReceivedEvent> getChoosableListener(List<Playlist> data) {
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

            int option = Integer.valueOf(message) - 1;
            if (data.size() < option || option > data.size()) {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());

            Playlist playlist = Queries.buildFullPlaylist(data.get(option).getUUID());
            if (playlist == null) {
                event.getChannel().sendMessage("Error occurred while accessing playlist").queue();
                return;
            }
            play(event.getChannel(), event.getMember(), playlist);
        };
    }

    @Override
    public Reaction<Page> getReactionListener(Page initialData) {
        Reaction<Page> listener = new Reaction<>(initialData);
        List<Playlist> playlists = (List<Playlist>) initialData.getExtra();
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

    private static void play(TextChannel textChannel, Member member,  Playlist playlist) {
        if (playlist.getTracks().size() < 1) {
            textChannel.sendMessage("Playlist is empty.").queue();
            return;
        }

        Set<AudioTrack> tracks = playlist.getTracks();
        Player player = PlayerManager.getOrCreate(textChannel, member.getVoiceState().getChannel());

        for (AudioTrack track : tracks) {
            PlayerManager.play(player, track, member, textChannel, true);
        }
        textChannel.sendMessage("Playlist `" + playlist.getName() + "` loaded").queue();
    }
}