package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;
import java.util.HashSet;

public class NotificationsCommand extends AbstractCommand {

    public NotificationsCommand() {
        super("notifications", new HashSet<>(Collections.singletonList("notify")), Collections.emptySet(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length != 1) {
            textChannel.sendMessage(Messages.COMMAND_NOTIFICATIONS_USAGE.get(guildInfo.getLang())).queue();
            return;
        }

        if (args[0].equals("off")) {
            guildInfo.setNotifications(false);
            textChannel.sendMessage(Messages.NOTIFICATIONS_TURNED_OFF.get(guildInfo.getLang())).queue();
        }
        else if (args[0].equals("on")) {
            guildInfo.setNotifications(true);
            textChannel.sendMessage(Messages.NOTIFICATIONS_TURNED_ON.get(guildInfo.getLang())).queue();
        }

        if (PlayerManager.isPlaying(textChannel)) {
            PlayerManager.getExisting(textChannel).setNotifications(guildInfo.notifications());
        }
    }
}
