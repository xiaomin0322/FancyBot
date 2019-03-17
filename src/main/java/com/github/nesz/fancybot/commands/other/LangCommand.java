package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;

public class LangCommand extends AbstractCommand {

    public LangCommand() {
        super("language", Collections.singletonList("lang"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length != 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_LANG_USAGE.get(guildInfo.getLang()));
            return;
        }

        if (textChannel.getGuild().getOwnerIdLong() != member.getUser().getIdLong()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_SERVER_OWNER.get(guildInfo.getLang()));
            return;
        }

        Lang lang = Arrays.stream(Lang.values()).filter(e -> e.name().equalsIgnoreCase(args[0])).findAny().orElse(null);

        if (lang == null) {
            lang = Arrays.stream(Lang.values()).filter(e -> e.getLocale().equalsIgnoreCase(args[0])).findAny().orElse(null);
        }

        if (lang == null) {
            MessagingHelper.sendAsync(textChannel, Messages.LANGUAGE_NOT_FOUND.get(guildInfo.getLang()));
            return;
        }

        guildInfo.setLang(lang);
        MessagingHelper.sendAsync(textChannel, Messages.LANGUAGE_CHANGED.get(guildInfo.getLang()).replace("{LANGUAGE}", lang.name()));

    }
}
