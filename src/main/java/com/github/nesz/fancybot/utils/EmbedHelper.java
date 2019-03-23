package com.github.nesz.fancybot.utils;

import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.objects.translation.Language;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;

public class EmbedHelper
{

    public static EmbedBuilder basicEmbed(final Color color, final CommandContext context)
    {
        final String footer = String.format("%s %s#%s | FancyBot %s",
                context.translate(Messages.INVOKED_BY),
                context.author().getName(),
                context.author().getDiscriminator(),
                Constants.VERSION);
        
        return new EmbedBuilder()
                .setColor(color)
                .setFooter(footer, context.author().getAvatarUrl());
    }

    public static EmbedBuilder basicEmbed(final Color color, final Member invoker, final Language language)
    {
        final String footer = String.format("%s %s#%s | FancyBot %s",
                Messages.INVOKED_BY.get(language),
                invoker.getUser().getName(),
                invoker.getUser().getDiscriminator(),
                Constants.VERSION);

        return new EmbedBuilder()
                .setColor(color)
                .setFooter(footer, invoker.getUser().getAvatarUrl());
    }
}
