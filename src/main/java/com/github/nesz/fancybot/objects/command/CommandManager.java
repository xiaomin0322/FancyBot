package com.github.nesz.fancybot.objects.command;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.Reflections;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager {

    private static final Map<String, AbstractCommand> commands = new HashMap<>();
    private static final Map<String, AbstractCommand> commandsAliases = new HashMap<>();
    private static final String PREFIX = ".";

    static {
        loadCommands();
        loadAliases();
    }

    public static boolean isCommand(String msg) {
        String command = filterPrefix(msg.trim().split(" ")[0]);
        return msg.startsWith(PREFIX) && (commands.containsKey(command) || commandsAliases.containsKey(command));
    }

    private static String filterPrefix(String command) {
        return command.substring(PREFIX.length());
    }

    private static void loadAliases() {
        for (AbstractCommand command : commands.values()) {
            for (String alias : command.getAliases()) {
                commandsAliases.put(alias, command);
            }
        }
    }

    private static void loadCommands() {
        try {
            Set<Class<? extends AbstractCommand>> classes = Reflections.getSubtypesOf("com.github.nesz.fancybot.commands", AbstractCommand.class);
            for (Class<? extends AbstractCommand> s : classes) {
                AbstractCommand c = s.getConstructor().newInstance();
                if (c.getCommandType() == CommandType.SUB) {
                    continue;
                }
                if (!commands.containsKey(c.getCommand())) {
                    commands.put(c.getCommand(), c);
                }
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException | ClassNotFoundException e) {
            FancyBot.LOG.error("Error while loading commands", e);
        }
    }

    public static void process(Message message, TextChannel channel, Member member, String incomingMessage) {
        Member self = channel.getGuild().getSelfMember();
        String[] input = incomingMessage.trim().split(" ");

        if (!self.hasPermission(channel, Permission.MESSAGE_WRITE)) {
            return;
        }

        String commandName = filterPrefix(input[0]).toLowerCase();
        AbstractCommand command = commands.containsKey(commandName) ? commands.get(commandName) : commandsAliases.get(commandName);

        boolean hasPermissions = true;
        Set<String> missingPermissions = new HashSet<>();
        for (Permission permission : command.getPermissions()) {
            if (self.hasPermission(permission)) {
                continue;
            }
            missingPermissions.add(permission.name());
            hasPermissions = false;
        }

        if (!hasPermissions) {
            GuildInfo guildInfo = GuildManager.getOrCreate(channel.getGuild());
            channel.sendMessage(Messages.NOT_ENOUGH_PERMISSIONS
                    .get(guildInfo.getLang())
                    .replace("{PERMISSIONS}", String.join(", ", missingPermissions)))
                    .queue();
            return;
        }

        command.execute(message, Arrays.copyOfRange(input, 1, input.length), channel, member);
    }
}
