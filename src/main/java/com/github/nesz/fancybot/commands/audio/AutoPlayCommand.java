package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.Arrays;

public class AutoPlayCommand extends Command
{

    public AutoPlayCommand()
    {
        super("autoplay", Arrays.asList("ap", "aplay"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.guildCache().setAutoPlay(!context.guildCache().isAutoPlay());
        }
        else
        {
            if (context.arg(0).equalsIgnoreCase("on"))
            {
                context.guildCache().setAutoPlay(true);
            }
            else if (context.arg(0).equalsIgnoreCase("off"))
            {
                context.guildCache().setAutoPlay(false);
            }
            else
            {
                context.respond(Messages.COMMAND_AUTO_PLAY_USAGE);
                return;
            }
        }

        if (context.guildCache().isAutoPlay())
        {
            context.respond(Messages.AUTO_PLAY_TURNED_ON);
        }
        else
        {
            context.respond(Messages.AUTO_PLAY_TURNED_OFF);
        }
    }
}