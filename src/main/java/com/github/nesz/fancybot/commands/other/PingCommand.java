package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;

import java.time.Duration;

public class PingCommand extends Command
{

    public PingCommand()
    {
        super("ping", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        context.respond("Wait...", message ->
        {
            final long ping = Duration.between(context.message().getTimeCreated(), message.getTimeCreated()).toMillis();
            message.editMessage(new MessageBuilder().appendWith("Ping {PINGS} ms", "{PING}", ping).build()).queue();
        });
    }
}
