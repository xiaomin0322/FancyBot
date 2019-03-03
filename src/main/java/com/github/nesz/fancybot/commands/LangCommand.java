package com.github.nesz.fancybot.commands;

import com.github.nesz.fancybot.objects.guild.GuildInfo;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LangCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "language";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Collections.singletonList("lang"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Lang Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Changes language for server. \n" +
                        "**Usage:** lang [LANG/LOCAL]    \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length < 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild().getIdLong());

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
