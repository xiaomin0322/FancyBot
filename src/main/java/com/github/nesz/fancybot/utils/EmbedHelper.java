package com.github.nesz.fancybot.utils;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public class EmbedHelper {

    public static EmbedBuilder basicEmbed(Color color, Member invoker) {
        MessageEmbed.Footer footer = getFooter(invoker);
        return new EmbedBuilder()
                .setColor(color)
                .setFooter(footer.getText(), footer.getIconUrl());
    }

    public static MessageEmbed.Footer getFooter(Member invoker) {
        User user = invoker.getUser();
        String invoked = Messages.INVOKED_BY.get(GuildManager.getOrCreate(invoker.getGuild().getIdLong()).getLang());
        return new MessageEmbed.Footer(
                invoked + " " + user.getName() + "#" + user.getDiscriminator() + " | FancyBot " + FancyBot.getVersion(),
                user.getAvatarUrl(),
                null
        );
    }

    public static String bold(String text) {
        return "**" + text + "**";
    }

    public static String asLink(String text, String link) {
        return "[" + text + "](" + link + ")";
    }
}
