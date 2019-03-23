package com.github.nesz.fancybot.objects.reactions;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ChoosableManager
{

    private static final Cache<Long, Consumer<GuildMessageReceivedEvent>> CHOOSABLES = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    public static void registerListener(final Long id, final Consumer<GuildMessageReceivedEvent> consumer)
    {
        CHOOSABLES.put(id, consumer);
    }

    public static Cache<Long, Consumer<GuildMessageReceivedEvent>> getChoosables()
    {
        return CHOOSABLES;
    }
}
