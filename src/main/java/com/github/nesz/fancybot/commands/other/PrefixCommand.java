package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class PrefixCommand extends AbstractCommand {

    public PrefixCommand() {
        super("prefix", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length != 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_PREFIX_USAGE.get(guildInfo.getLang()));
            return;
        }

        if (args[0].length() > 4) {
            MessagingHelper.sendAsync(textChannel, Messages.PREFIX_CANNOT_BE_LONGER_THAN_4_CHARS.get(guildInfo.getLang()));
            return;
        }

        if (textChannel.getGuild().getOwnerIdLong() != member.getUser().getIdLong()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_SERVER_OWNER.get(guildInfo.getLang()));
            return;
        }

        guildInfo.setPrefix(args[0]);
        MessagingHelper.sendAsync(textChannel, Messages.LANGUAGE_CHANGED.get(guildInfo.getLang()).replace("{PREFIX}", guildInfo.getPrefix()));
    }
}