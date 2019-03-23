package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.utils.RandomUtil;

public class OwoCommand extends Command
{

    public OwoCommand()
    {
        super("owo", CommandType.PARENT);
    }

    private static final String[] FACES = new String[] { "(・`ω´・)", ";;w;;", "owo", "UwU", ">w<", "^w^" };

    @Override
    public void execute(final CommandContext context)
    {
        final String message = context.message().getContentRaw()
                .replace("r", "w")
                .replace("R", "W")
                .replace("l", "w")
                .replace("L", "W")
                .replace("n", "ny")
                .replace("N", "NY")
                .replace("ove", "uv")
                .replace("!", " " + FACES[RandomUtil.getRandomIntBetween(0, FACES.length - 1)]);

        context.respond(message);
    }
}