package com.github.nesz.fancybot.objects.reactions;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Consumer;

public class Reaction<T> {

    private final LinkedHashMap<Emote, Consumer<Message>> reactions;
    private volatile T data;

    public Reaction(T data) {
        this.data = data;
        this.reactions = new LinkedHashMap<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Consumer<Message> getConsumer(MessageReaction.ReactionEmote emote) {
        return reactions.keySet().stream().filter(e -> e.getId().equalsIgnoreCase(emote.getId())).findFirst().map(reactions::get).orElse(null);
    }

    public void registerReaction(Emote emote, Consumer<Message> consumer) {
        reactions.put(emote, consumer);
    }

    public Set<Emote> getEmotes() {
        return reactions.keySet();
    }

}
