package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        handleReaction(event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        handleReaction(event);
    }

    private void handleReaction(GenericMessageReactionEvent event) {
        FancyBot.LOG.debug("hereeeeee");
        FancyBot.LOG.debug(event.getUser().getName());
        if (event.getUser().isBot()) {
            return;
        }
        if (event.getGuild().getSelfMember().equals(event.getMember())) {
            return;
        }
        TextChannel channel = (TextChannel) event.getChannel();
        Message message = channel.getMessageById(event.getMessageIdLong()).complete();
        ReactionManager.handle(message, event.getReaction());
    }
}