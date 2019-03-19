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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RewindCommand extends AbstractCommand {

    public RewindCommand() {
        super("rewind", Arrays.asList("re", "r"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length != 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_REWIND_USAGE.get(lang));
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_REWIND_USAGE.get(lang));
            return;
        }

        if (!PlayerManager.isPlaying(textChannel)) {
            MessagingHelper.sendAsync(textChannel, Messages.MUSIC_NOT_PLAYING.get(lang));
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang));
            return;
        }

        AudioTrack track = player.getAudioPlayer().getPlayingTrack();
        long ff = TimeUnit.SECONDS.toMillis(Long.parseLong(args[0]));
        long position = track.getPosition();
        long rewinded = position - ff;
        if (rewinded <= 0) {
            if (player.getPreviousTracks().isEmpty()) {
                track.setPosition(0);
                return;
            }
            player.previousTrack();
            return;
        }
        track.setPosition(rewinded);
    }
}