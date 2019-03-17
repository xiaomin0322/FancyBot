package com.github.nesz.fancybot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public abstract class AbstractCommand {

    private final String command;
    private final List<String> aliases;
    private final List<Permission> permissions;
    private final CommandType commandType;

    public AbstractCommand(String command, List<String> aliases, List<Permission> permissions, CommandType commandType) {
        this.command = command;
        this.aliases = aliases;
        this.permissions = permissions;
        this.commandType = commandType;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public abstract void execute(Message message, String[] args, TextChannel textChannel, Member member);
}