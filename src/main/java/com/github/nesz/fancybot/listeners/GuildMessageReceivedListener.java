package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.command.CommandManager;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildMessageReceivedListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw();
        FancyBot.LOG.debug(rawMessage);
        if (rawMessage.isEmpty()) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!CommandManager.isCommand(rawMessage)) {
            return;
        }
        CommandManager.process(event.getMessage(), event.getChannel(), event.getMember(), rawMessage);
    }
}