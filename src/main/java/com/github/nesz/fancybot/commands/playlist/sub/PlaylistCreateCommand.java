package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.playlist.PlaylistManager;

public class PlaylistCreateCommand extends Command
{

    public PlaylistCreateCommand()
    {
        super("create", CommandType.CHILD);
    }

    @Override
    public void execute(final CommandContext context)
    {
        final String name = String.join(" ", context.args());

        if (name.length() > Constants.PLAYLIST_NAME_LENGTH_MAX)
        {
            context.respond("Playlist name cannot be longer than 32 characters");
            return;
        }

        if (name.length() < Constants.PLAYLIST_NAME_LENGTH_MIN)
        {
            context.respond("Playlist name cannot be longer than 3 characters");
            return;
        }

        final boolean success = PlaylistManager.create(name, context.author().getIdLong());

        if (success)
        {
            context.respond("Playlist '" + name + "' created.");
        }
        else
        {
            context.respond("Error occurred while creating playlist, try again later");
        }

    }
}