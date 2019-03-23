package com.github.nesz.fancybot.commands;

import com.github.nesz.fancybot.objects.guild.GuildCache;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Language;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.function.Consumer;

public class CommandContext
{

    private final GuildMessageReceivedEvent event;
    private final GuildCache guildCache;
    private final String[] args;

    public CommandContext(final GuildMessageReceivedEvent event, final String[] args)
    {
        this.guildCache = GuildManager.getOrCreate(event.getGuild());
        this.event = event;
        this.args = args;
    }

    public GuildMessageReceivedEvent event()
    {
        return event;
    }

    public String arg(final int i)
    {
        return args[i];
    }

    public String[] args()
    {
        return args;
    }

    public boolean hasArgs()
    {
        return args.length >= 1;
    }

    public Message message()
    {
        return event.getMessage();
    }

    public Guild guild()
    {
        return event.getGuild();
    }

    public TextChannel channel()
    {
        return event.getChannel();
    }

    public Member member()
    {
        return event.getMember();
    }

    public User author()
    {
        return event.getAuthor();
    }

    public Language language()
    {
        return guildCache.getLanguage();
    }

    public GuildCache guildCache()
    {
        return guildCache;
    }

    public String translate(final Messages messages)
    {
        return messages.get(language());
    }

    public void respond(final Messages messages)
    {
        MessagingHelper.sendAsync(channel(), translate(messages));
    }

    public void respond(final Messages messages, final Consumer<Message> consumer)
    {
        MessagingHelper.sendAsync(channel(), translate(messages), consumer);
    }

    public void respond(final String message)
    {
        MessagingHelper.sendAsync(channel(), message);
    }

    public void respond(final String message, final Consumer<Message> consumer)
    {
        MessagingHelper.sendAsync(channel(), message, consumer);
    }

    public void respond(final EmbedBuilder message)
    {
        MessagingHelper.sendAsync(channel(), message);
    }

    public void respond(final MessageEmbed message)
    {
        MessagingHelper.sendAsync(channel(), message);
    }

    public void respond(final MessageEmbed message, final Consumer<Message> consumer)
    {
        MessagingHelper.sendAsync(channel(), message, consumer);
    }


}
