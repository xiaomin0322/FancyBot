package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.command.CommandManager;
import com.github.nesz.fancybot.objects.reactions.ChoosableManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.function.Consumer;

public class GuildMessageReceivedListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        User author = event.getAuthor();

        if (rawMessage.isEmpty()) {
            return;
        }

        if (author.isBot()) {
            return;
        }

        FancyBot.LOG.debug(rawMessage);

        Consumer<GuildMessageReceivedEvent> consumer = ChoosableManager.getChoosables().getIfPresent(author.getIdLong());
        if (consumer != null) {
            consumer.accept(event);
        }
        if (!CommandManager.isCommand(rawMessage)) {
            return;
        }
        CommandManager.process(event.getMessage(), event.getChannel(), event.getMember(), rawMessage);
    }
}