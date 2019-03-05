package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class LangCommand extends AbstractCommand {

    public LangCommand() {
        super("language", new HashSet<>(Collections.singletonList("lang")), Collections.emptySet(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_LANG_USAGE.get(guildInfo.getLang())).queue();
            return;
        }

        Lang lang = Arrays.stream(Lang.values()).filter(e -> e.name().equalsIgnoreCase(args[0])).findAny().orElse(null);

        if (lang == null) {
            lang = Arrays.stream(Lang.values()).filter(e -> e.getLocale().equalsIgnoreCase(args[0])).findAny().orElse(null);
        }

        if (lang == null) {
            textChannel.sendMessage(Messages.LANGUAGE_NOT_FOUND.get(guildInfo.getLang())).queue();
            return;
        }

        guildInfo.setLang(lang);
        textChannel.sendMessage(Messages.LANGUAGE_CHANGED.get(guildInfo.getLang()).replace("{LANGUAGE}", lang.name())).queue();
    }
}
