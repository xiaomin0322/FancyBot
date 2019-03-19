package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;

public class NotificationsCommand extends AbstractCommand {

    public NotificationsCommand() {
        super("notifications", Arrays.asList("notify", "alerts"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length != 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_NOTIFICATIONS_USAGE.get(guildInfo.getLang()));
            return;
        }

        if (args[0].equals("off")) {
            guildInfo.setNotifications(false);
            MessagingHelper.sendAsync(textChannel, Messages.NOTIFICATIONS_TURNED_OFF.get(guildInfo.getLang()));
            return;
        }
        if (args[0].equals("on")) {
            guildInfo.setNotifications(true);
            MessagingHelper.sendAsync(textChannel, Messages.NOTIFICATIONS_TURNED_ON.get(guildInfo.getLang()));
        }
    }
}
