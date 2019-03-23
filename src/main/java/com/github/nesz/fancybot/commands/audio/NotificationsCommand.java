package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.Arrays;

public class NotificationsCommand extends Command
{

    public NotificationsCommand()
    {
        super("notifications", Arrays.asList("notify", "alerts"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (context.hasArgs())
        {
            context.respond(Messages.COMMAND_NOTIFICATIONS_USAGE);
            return;
        }

        if (context.arg(0).equalsIgnoreCase("off"))
        {
            context.guildCache().setNotifications(false);
            context.respond(Messages.NOTIFICATIONS_TURNED_OFF);
            return;
        }

        if (context.arg(0).equalsIgnoreCase("on"))
        {
            context.guildCache().setNotifications(true);
            context.respond(Messages.NOTIFICATIONS_TURNED_ON);
            return;
        }

        context.respond(Messages.COMMAND_NOTIFICATIONS_USAGE);
    }
}
