package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.playlist.Playlist;
import com.github.nesz.fancybot.objects.playlist.PlaylistManager;
import com.github.nesz.fancybot.objects.reactions.*;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PlaylistDeleteCommand extends Command implements Choosable<List<Playlist>>, Interactable<Page>
{

    public PlaylistDeleteCommand()
    {
        super("delete", CommandType.CHILD);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (context.args().length == 1 && StringUtils.isUUID(context.arg(0)))
        {
            if (!Queries.exists(UUID.fromString(context.arg(0))))
            {
                context.respond("Playlist with that ID doesn't exists");
                return;
            }

            final boolean success = PlaylistManager.delete(UUID.fromString(context.arg(0)));

            if (success)
            {
                context.respond("Successfully deleted playlist with ID `" + context.arg(0) + "`");
                return;
            }

            context.respond("Error occurred while deleting playlist, not all data might be deleted");
            return;
        }

        final String name = String.join(" ", context.args());

        if (name.length() > Constants.PLAYLIST_NAME_LENGTH_MAX)
        {
            context.respond("Playlist name cannot be longer than 32 characters");
            return;
        }

        final List<Playlist> playlists = Queries.selectMinimizedPlaylists(name);

        if (playlists.isEmpty())
        {
            context.respond("Playlist with that name doesn't exists");
            return;
        }

        if (playlists.size() == 1)
        {
            final boolean success = PlaylistManager.delete(playlists.get(0).getUUID());

            if (success)
            {
                context.respond("Successfully deleted playlist `" + name + "`");
                return;
            }

            context.respond("Error occurred while deleting playlist, not all data might be deleted");
            return;
        }

        final int maxPage = (int) Math.ceil((double) playlists.size() / (double) ITEMS_PER_PAGE);
        final Object[] extra = { playlists, context };
        context.respond(printQueue(context, playlists, 1), msg ->
        {
            if (maxPage > 1) ReactionManager.addListener(msg, getReactionListener(new Page<>(1, maxPage, extra)));
            msg.delete().queueAfter(1, TimeUnit.MINUTES);
        });

        ChoosableManager.registerListener(context.author().getIdLong(), getChoosableListener(playlists));
    }

    @Override
    public Consumer<GuildMessageReceivedEvent> getChoosableListener(final List<Playlist> data)
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

            final int option = Integer.valueOf(message) - 1;

            if (data.size() < option || option > data.size())
            {
                event.getChannel().sendMessage("invalid option").queue();
                return;
            }

            ChoosableManager.getChoosables().invalidate(event.getAuthor().getIdLong());

            final Playlist playlist = data.get(option);
            final boolean success = PlaylistManager.delete(playlist.getUUID());

            if (success)
            {
                event.getChannel().sendMessage("Successfully deleted playlist `" + playlist.getName() + "`").queue();
                return;
            }

            event.getChannel().sendMessage("Error occurred while deleting playlist, not all data might be deleted").queue();
        };
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