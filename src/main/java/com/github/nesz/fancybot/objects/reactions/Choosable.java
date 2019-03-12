package com.github.nesz.fancybot.objects.reactions;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.function.Consumer;

public interface Choosable<T> {

    Consumer<GuildMessageReceivedEvent> getChoosableListener(T data);

}
