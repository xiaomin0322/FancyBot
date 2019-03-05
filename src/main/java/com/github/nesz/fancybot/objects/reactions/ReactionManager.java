package com.github.nesz.fancybot.objects.reactions;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReactionManager {

    private static final Cache<Long, Reaction<?>> REACTIONS = Caffeine.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();

    public static void addListener(Message message, Reaction<?> handler) {
        REACTIONS.put(message.getIdLong(), handler);
        for (Emote emote : handler.getEmotes()) {
            message.addReaction(emote.asSnowflake()).queue();
        }
    }

    public static void handle(Message message, MessageReaction reaction) {
        Reaction<?> listener = REACTIONS.getIfPresent(message.getIdLong());
        if (listener == null) {
            return;
        }
        Consumer<Message> action = listener.getConsumer(reaction.getReactionEmote());
        if (action == null) {
            return;
        }
        action.accept(message);
    }
}
