package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.objects.command.CommandManager;
import com.github.nesz.fancybot.objects.guild.GuildCache;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.ChoosableManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.function.Consumer;

public class GuildMessageReceivedListener extends ListenerAdapter
{

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event)
    {
        final String rawMessage = event.getMessage().getContentRaw();
        final User author = event.getAuthor();


        if (rawMessage.isEmpty())
        {
            return;
        }

        if (author.isBot())
        {
            return;
        }

        if (MessagingHelper.isUserMention(rawMessage)) {
            if (event.getJDA().getSelfUser().getId().equalsIgnoreCase(MessagingHelper.extractId(rawMessage)))
            {
                final GuildCache cache = GuildManager.getOrCreate(event.getGuild());
                final EmbedBuilder eb = EmbedHelper.basicEmbed(Color.ORANGE, event.getMember(), cache.getLanguage());
                eb.setTitle("Hey there!");
                eb.setDescription(Messages.SELF_MENTION_MESSAGE.get(cache.getLanguage())
                        .replace("{PREFIX}", cache.getPrefix()));
                MessagingHelper.sendAsync(event.getChannel(), eb);
                return;
            }
        }

        final Consumer<GuildMessageReceivedEvent> consumer = ChoosableManager.getChoosables()
                .getIfPresent(author.getIdLong());

        if (consumer != null)
        {
            consumer.accept(event);
        }

        CommandManager.handle(event);
    }
}