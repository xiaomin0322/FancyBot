package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.Interactable;
import com.github.nesz.fancybot.objects.reactions.Reaction;
import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaylistListCommand extends AbstractCommand implements Interactable<Page> {

    public PlaylistListCommand() {
        super("list", Collections.emptyList(), Collections.emptyList(), CommandType.SUB);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();

        List<Playlist> playlists = Queries.selectMinimizedPlaylists();
        if (playlists.isEmpty()) {
            textChannel.sendMessage("There are no playlists").queue();
            return;
        }

        int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);
        textChannel.sendMessage(printQueue(textChannel, EmbedHelper.getFooter(member), playlists, 1)).queue(msg -> {
            if (maxPage > 1) {
                ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, textChannel, playlists)));
            }
            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });
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

        return eb.build();
    }
}