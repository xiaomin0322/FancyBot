package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.translation.Language;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class LanguageCommand extends Command
{

    public LanguageCommand()
    {
        super("language", Collections.singletonList("lang"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_LANG_USAGE);
            return;
        }

        if (context.guild().getOwnerIdLong() != context.author().getIdLong())
        {
            context.respond(Messages.YOU_HAVE_TO_BE_SERVER_OWNER);
            return;
        }

        Optional<Language> language = Arrays.stream(Language.values())
                .filter(e -> e.name().equalsIgnoreCase(context.arg(0)))
                .findAny();

        if (!language.isPresent())
        {
            language = Arrays.stream(Language.values())
                    .filter(e -> e.getLocale().equalsIgnoreCase(context.arg(0)))
                    .findAny();
        }

        if (!language.isPresent())
        {
            context.respond(Messages.LANGUAGE_NOT_FOUND);
            return;
        }

        context.guildCache().setLanguage(language.get());
        context.respond(context.translate(Messages.LANGUAGE_CHANGED)
               .replace("{LANGUAGE}", language.get().name()));

    }
}
