package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;

public class AutoPlayCommand extends AbstractCommand {

    public AutoPlayCommand() {
        super("autoplay", Arrays.asList("ap", "aplay"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                guildInfo.setAutoPlay(true);
            }
            else if (args[0].equalsIgnoreCase("off")) {
                guildInfo.setAutoPlay(false);
            }
            else {
                MessagingHelper.sendAsync(textChannel, Messages.COMMAND_AUTO_PLAY_USAGE.get(guildInfo.getLang()));
                return;
            }
        }
        else {
            guildInfo.setAutoPlay(!guildInfo.isAutoPlay());
        }
        if (guildInfo.isAutoPlay()) {
            MessagingHelper.sendAsync(textChannel, Messages.AUTO_PLAY_TURNED_ON.get(guildInfo.getLang()));
        }
        else {
            MessagingHelper.sendAsync(textChannel, Messages.AUTO_PLAY_TURNED_OFF.get(guildInfo.getLang()));
        }
    }
}