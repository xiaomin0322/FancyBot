package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.managers.AudioManager;

import java.lang.management.ManagementFactory;

public class StatsCommand extends Command
{

    private static final OperatingSystemMXBean osb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final Runtime rt = Runtime.getRuntime();

    public StatsCommand()
    {
        super("stats", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        final long free = rt.freeMemory() / 1024 / 1024;
        final long total = rt.totalMemory() / 1024 / 1024;
        final long used = total - free;

        final long audios = context.event()
                .getJDA()
                .getAudioManagers()
                .stream()
                .filter(AudioManager::isConnected)
                .count();

        context.respond(new MessageBuilder()
                .append("```css")
                .breakLine()
                .appendWith("Bot version: {VERSION}", "{VERSION}", Constants.VERSION)
                .breakLine()
                .appendWith("JDA version: {VERSION}", "{VERSION}", JDAInfo.VERSION)
                .breakLine()
                .appendWith("Guilds: {GUILDS}", "{GUILDS}", FancyBot.getShardManager().getGuilds().size())
                .breakLine()
                .appendWith("Shard: {SHARD}", "{SHARD}", context.event().getJDA().getShardInfo())
                .breakLine()
                .appendWith("Audio connections (curr. shard): {AUDIO}", "{AUDIO}", audios)
                .breakLine()
                .appendWith("CPU usage: {CPU}%", "{CPU}", Math.floor(osb.getProcessCpuLoad() * 100))
                .breakLine()
                .append("Memory usage:")
                .breakLine()
                .appendWith("Free: {FREE}MB", "{FREE}", free)
                .breakLine()
                .appendWith("Allocated: {ALLOC}MB", "{ALLOC}", total)
                .breakLine()
                .appendWith("Used: {USED}MB", "{USED}", used)
                .breakLine()
                .append("```")
                .build()
        );
    }
}