package com.github.nesz.fancybot.commands.playlist;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.commands.playlist.sub.*;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.*;

public class PlaylistCommand extends Command
{

    public PlaylistCommand()
    {
        super("playlist", Collections.singletonList("pl"), CommandType.PARENT);
    }

    private static final Set<Command> CHILDREN = new HashSet<>(Arrays.asList(
            new PlaylistCreateCommand(),
            new PlaylistDeleteCommand(),
            new PlaylistAddCommand(),
            new PlaylistPlayCommand(),
            new PlaylistListCommand())
    );

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_PLAYLIST_USAGE);
            return;
        }

        final Optional<Command> child = CHILDREN.stream()
                .filter(c -> c.getCommand().equalsIgnoreCase(context.arg(0)) || c.getAliases().contains(context.arg(0)))
                .findFirst();

        if (!child.isPresent())
        {
            context.respond(Messages.COMMAND_PLAYLIST_USAGE);
            return;
        }


        child.get().execute(new CommandContext(
                context.event(),
                Arrays.copyOfRange(context.args(), 1, context.args().length)
        ));
    }
}