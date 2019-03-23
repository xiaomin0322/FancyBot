package com.github.nesz.fancybot.commands;

import net.dv8tion.jda.api.Permission;

import java.util.Collections;
import java.util.List;

public abstract class Command
{

    private final String command;
    private final List<String> aliases;
    private final List<Permission> permissions;
    private final CommandType commandType;

    public Command(final String command, final List<String> aliases, final List<Permission> permissions, final CommandType commandType)
    {
        this.command = command;
        this.aliases = aliases;
        this.permissions = permissions;
        this.commandType = commandType;
    }

    public Command(final String command, final List<String> aliases, final CommandType commandType)
    {
        this(command, aliases, Collections.emptyList(), commandType);
    }

    public Command(final String command, final CommandType commandType)
    {
        this(command, Collections.emptyList(), Collections.emptyList(), commandType);
    }

    public String getCommand()
    {
        return command;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public List<Permission> getPermissions()
    {
        return permissions;
    }

    public CommandType getType()
    {
        return commandType;
    }

    public abstract void execute(CommandContext context);

}
