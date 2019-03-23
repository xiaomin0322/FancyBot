package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.translation.Messages;

public class PrefixCommand extends Command
{

    public PrefixCommand()
    {
        super("prefix", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_PREFIX_USAGE);
            return;
        }

        if (context.arg(0).length() > 4)
        {
            context.respond(Messages.PREFIX_CANNOT_BE_LONGER_THAN_4_CHARS);
            return;
        }

        if (context.guild().getOwnerIdLong() != context.author().getIdLong())
        {
            context.respond(Messages.YOU_HAVE_TO_BE_SERVER_OWNER);
            return;
        }

        context.guildCache().setPrefix(context.arg(0));
        context.respond(context.translate(Messages.LANGUAGE_CHANGED)
               .replace("{PREFIX}", context.guildCache().getPrefix()));
    }
}