package com.github.nesz.fancybot.objects.command;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildCache;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.Reflections;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandManager
{

    private static final ExecutorService COMMAND_POOL = Executors.newCachedThreadPool();
    private static final Map<String, Command> COMMANDS = new ConcurrentHashMap<>();

    public static Set<String> getCommands()
    {
        return COMMANDS.keySet();
    }

    private static boolean isCommand(final String msg, final GuildCache guildCache)
    {
        final String command = filterPrefix(msg.trim().split(" ")[0], guildCache);
        return msg.startsWith(guildCache.getPrefix()) && COMMANDS.containsKey(command);
    }

    private static String filterPrefix(final String command, final GuildCache guildCache)
    {
        return command.substring(guildCache.getPrefix().length());
    }

    public static void loadCommands(final String pckg)
    {
        try
        {
            final Set<Class<? extends Command>> classes = Reflections.getSubtypesOf(pckg, Command.class);

            for (final Class<? extends Command> clazz : classes)
            {
                final Command command = clazz.getConstructor().newInstance();
                if (command.getType() == CommandType.CHILD)
                {
                    continue;
                }
                COMMANDS.put(command.getCommand(), command);
                command.getAliases().forEach(s -> COMMANDS.put(s, command));
            }

        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            FancyBot.LOGGER.error("[CommandManager] Error occurred while loading commands", e);
        }
    }

    public static void handle(final GuildMessageReceivedEvent event)
    {
        final Member self = event.getGuild().getSelfMember();
        final TextChannel channel = event.getChannel();
        final Message message = event.getMessage();
        final GuildCache guildCache = GuildManager.getOrCreate(channel.getGuild());

        if (!isCommand(message.getContentRaw(), guildCache))
        {
            return;
        }

        if (!self.hasPermission(channel, Permission.MESSAGE_WRITE))
        {
            return;
        }

        final String[] input = message.getContentRaw().trim().split(" ");
        final String commandName = filterPrefix(input[0], guildCache).toLowerCase();
        final Command command = COMMANDS.get(commandName);

        boolean hasPermissions = true;
        final Set<String> missingPermissions = new HashSet<>();

        for (final Permission permission : command.getPermissions())
        {
            if (self.hasPermission(permission))
            {
                continue;
            }
            missingPermissions.add(permission.name());
            hasPermissions = false;
        }

        if (!hasPermissions)
        {
            channel.sendMessage(Messages.NOT_ENOUGH_PERMISSIONS
                    .get(guildCache.getLanguage())
                    .replace("{PERMISSIONS}", String.join(", ", missingPermissions)))
                    .queue();
            return;
        }

        COMMAND_POOL.submit(() ->
        {
            final CommandContext commandContext = new CommandContext(event, Arrays.copyOfRange(input, 1, input.length));
            command.execute(commandContext);
        });
    }
}
