package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.command.CommandManager;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.RandomUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class HelpCommand extends Command
{

    public HelpCommand()
    {
        super("help", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        final EmbedBuilder eb = EmbedHelper.basicEmbed(RandomUtil.getRandomColor(), context);
        CommandManager.getCommands().forEach(command -> {
            eb.appendDescription("`" + command + "`, ");
        });

        context.respond(eb);
    }
}