package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Collections;
import java.util.Set;

public class StopCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public Set<String> getAliases() {
        return Collections.emptySet();
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Stop Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Stops music. \n" +
                        "**Usage:** stop \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (!PlayerManager.isPlaying(textChannel)) {
            Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(lang)).queue();
            return;
        }
        PlayerManager.stop(textChannel);
    }
}