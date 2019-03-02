package com.github.nesz.fancybot.commands;

import net.dv8tion.jda.core.entities.*;

import java.util.List;

public abstract class AbstractCommand {

    public abstract String getCommand();

    public abstract MessageEmbed getUsage();

    public abstract List<String> getAliases();

    public abstract void execute(Message message, String[] args, TextChannel textChannel, Member member);
}