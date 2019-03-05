package com.github.nesz.fancybot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

public abstract class AbstractCommand {

    private final String command;
    private final Set<String> aliases;
    private final Set<Permission> permissions;
    private final CommandType commandType;

    public AbstractCommand(String command, Set<String> aliases, Set<Permission> permissions, CommandType commandType) {
        this.command = command;
        this.aliases = aliases;
        this.permissions = permissions;
        this.commandType = commandType;
    }

    public String getCommand() {
        return command;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public abstract void execute(Message message, String[] args, TextChannel textChannel, Member member);
}