package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collections;
import java.util.HashSet;

public class VolumeCommand extends AbstractCommand {

    public VolumeCommand() {
        super("volume", new HashSet<>(Collections.singletonList("vol")), Collections.emptySet(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild());
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_VOLUME_USAGE.get(guildInfo.getLang())).queue();
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            textChannel.sendMessage(Messages.COMMAND_VOLUME_USAGE.get(guildInfo.getLang())).queue();
            return;
        }

        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(guildInfo.getLang())).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(guildInfo.getLang())).queue();
            return;
        }

        int volume = Integer.parseInt(args[0]);
        if (volume < 0) {
            volume = 0;
        }

        textChannel.sendMessage(Messages.MUSIC_VOLUME_CHANGED.get(guildInfo.getLang()).replace("{VOLUME}", String.valueOf(volume))).queue();
        guildInfo.setVolume(volume);
        player.getAudioPlayer().setVolume(volume);
        player.setVolume(volume);
    }
}