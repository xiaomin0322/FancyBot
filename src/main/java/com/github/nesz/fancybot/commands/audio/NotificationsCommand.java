package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NotificationsCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "notifications";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Collections.singletonList("notify"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Notifications Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Turn on/off notifications. \n" +
                        "**Usage:** notifications [ON/OFF] \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length != 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild().getIdLong());
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
