package com.github.nesz.fancybot.objects.reactions;

public interface Interactive<T> {

    Reaction<T> getReactionListener(T initialData);

}