package com.github.nesz.fancybot.objects.reactions;

public interface Interactable<T>
{

    Reaction<T> getReactionListener(T initialData);

}