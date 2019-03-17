package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class StopCommand extends AbstractCommand {

    public StopCommand() {
        super("stop", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (!PlayerManager.isPlaying(textChannel)) {
            Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
            MessagingHelper.sendAsync(textChannel, Messages.MUSIC_NOT_PLAYING.get(lang));
            return;
        }
        PlayerManager.stop(textChannel);
    }
}