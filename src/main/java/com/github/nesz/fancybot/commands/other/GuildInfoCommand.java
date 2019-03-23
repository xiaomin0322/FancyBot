package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;

import java.util.Collections;

public class GuildInfoCommand extends Command
{

    public GuildInfoCommand()
    {
        super("guildinfo", Collections.singletonList("ginfo"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        context.respond(new MessageBuilder()
                .appendWith("Language `{LANGUAGE}`", "{LANGUAGE}", context.language().name())
                .breakLine()
                .appendWith("Prefix: `{PREFIX}`", "{PREFIX}", context.guildCache().getPrefix())
                .breakLine()
                .appendWith("Volume: `{VOLUME}`", "{VOLUME}", context.guildCache().getVolume())
                .breakLine()
                .appendWith("Autoplay: `{AUTOPLAY}`", "{AUTOPLAY}", context.guildCache().isAutoPlay())
                .breakLine()
                .appendWith("Track notifications: `{NOTIFY}`", "{NOTIFY}", context.guildCache().notifications())
                .build());
    }
}
