package com.github.nesz.fancybot.commands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Set;

public abstract class AbstractCommand {

    public abstract String getCommand();

    public abstract MessageEmbed getUsage();

    public abstract Set<String> getAliases();

    public abstract Set<Permission> getRequiredPermissions();

    public abstract void execute(Message message, String[] args, TextChannel textChannel, Member member);
}