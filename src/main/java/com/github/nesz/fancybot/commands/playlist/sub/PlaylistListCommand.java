package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.Interactable;
import com.github.nesz.fancybot.objects.reactions.Reaction;
import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlaylistListCommand extends Command implements Interactable<Page>
{

    public PlaylistListCommand()
    {
        super("list", CommandType.CHILD);
    }

    @Override
    public void execute(final CommandContext context)
    {
        final List<Playlist> playlists = Queries.selectMinimizedPlaylists();

        if (playlists.isEmpty())
        {
            context.respond("There are no playlists");
            return;
        }

        final Object[] extra = { playlists, context };
        final int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);

        context.respond(printQueue(context, playlists, 1), msg ->
        {
            if (maxPage > 1) ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, extra)));
            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public Reaction<Page> getReactionListener(final Page initialData)
    {
        final Reaction<Page> listener = new Reaction<>(initialData);
        final Object[] extra = (Object[]) initialData.getExtra();
        final List<Playlist> playlists = (List<Playlist>) extra[0];
        final CommandContext context = (CommandContext) extra[1];

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
}