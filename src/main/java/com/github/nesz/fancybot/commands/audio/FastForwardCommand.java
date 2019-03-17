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

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class FastForwardCommand extends AbstractCommand {

    public FastForwardCommand() {
        super("fastforward", Collections.singletonList("ff"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length != 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_FAST_FORWARD_USAGE.get(lang));
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_FAST_FORWARD_USAGE.get(lang));
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
        long duration = track.getDuration();
        long forwarded = position + ff;
        if (forwarded >= duration) {
            player.nextTrack();
            return;
        }
        track.setPosition(forwarded);
    }
}