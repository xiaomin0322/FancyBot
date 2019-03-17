package com.github.nesz.fancybot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagingHelper {

    public static void sendAsync(TextChannel textChannel, String message, Consumer<Message> consumer) {
        textChannel.sendMessage(message).queue(m -> {
            m.delete().queueAfter(10, TimeUnit.MINUTES);
            consumer.accept(m);
        });
    }

    public static void sendAsync(TextChannel textChannel, String message) {
        textChannel.sendMessage(message).queue(m ->
            m.delete().queueAfter(10, TimeUnit.MINUTES)
        );
    }

    public static void sendAsync(TextChannel textChannel, MessageEmbed message) {
        textChannel.sendMessage(message).queue(m ->
            m.delete().queueAfter(10, TimeUnit.MINUTES)
        );
    }

    public static void sendAsync(TextChannel textChannel, MessageEmbed message, Consumer<Message> consumer) {
        textChannel.sendMessage(message).queue(m -> {
            m.delete().queueAfter(10, TimeUnit.MINUTES);
            consumer.accept(m);
        });
    }

    public static void sendAsync(TextChannel textChannel, EmbedBuilder message) {
        sendAsync(textChannel, message.build());
    }

    public static void sendAsync(TextChannel textChannel, EmbedBuilder message, Consumer<Message> consumer) {
        sendAsync(textChannel, message.build(), consumer);
    }

    private static final Pattern MENTION_ANY = Pattern.compile("<[@#][&!]?([0-9]{4,})>");
    private static final Pattern MENTION_USER = Pattern.compile("<@!?([0-9]{8,})>");

    public static String extractId(String mention) {
        Matcher matcher = MENTION_ANY.matcher(mention);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static boolean isUserMention(String input) {
        return MENTION_USER.matcher(input).find();
    }
}
