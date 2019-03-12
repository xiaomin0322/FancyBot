package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
        if (event.getUser().isBot()) {
            return;
        }
        if (event.getGuild().getSelfMember().equals(event.getMember())) {
            return;
        }
        TextChannel channel = (TextChannel) event.getChannel();

        Message message = channel.retrieveMessageById(event.getMessageIdLong()).complete();
        ReactionManager.handle(message, event.getReaction());
    }
}