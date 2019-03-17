package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class VolumeCommand extends AbstractCommand {

    public VolumeCommand() {
        super("volume", Collections.singletonList("vol"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length < 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_VOLUME_USAGE.get(guildInfo.getLang()));
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_VOLUME_USAGE.get(guildInfo.getLang()));
            return;
        }

        int volume = Integer.parseInt(args[0]);
        if (volume < 0) {
            volume = 0;
        }

        MessagingHelper.sendAsync(textChannel, Messages.MUSIC_VOLUME_CHANGED.get(guildInfo.getLang()).replace("{VOLUME}", String.valueOf(volume)));
        guildInfo.setVolume(volume);

        if (PlayerManager.isPlaying(textChannel)) {
            Player player = PlayerManager.getExisting(textChannel);
            player.getAudioPlayer().setVolume(volume);
        }
    }
}