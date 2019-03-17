package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class SkipCommand extends AbstractCommand {

    public SkipCommand() {
        super("skip", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (!PlayerManager.isPlaying(textChannel)) {
            MessagingHelper.sendAsync(textChannel, Messages.MUSIC_NOT_PLAYING.get(lang));
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang));
            return;
        }

        if (args.length == 1) {
            if (!StringUtils.isNumeric(args[0])) {
                MessagingHelper.sendAsync(textChannel, Messages.COMMAND_SKIP_USAGE.get(lang));
                return;
            }

            player.skip(Integer.valueOf(args[0]));
            return;
        }

        player.nextTrack();
    }
}