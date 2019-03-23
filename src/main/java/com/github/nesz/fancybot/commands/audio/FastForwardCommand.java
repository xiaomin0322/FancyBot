package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class FastForwardCommand extends Command
{

    public FastForwardCommand()
    {
        super("fastforward", Collections.singletonList("ff"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!PlayerManager.isPlaying(context.guild()))
        {
            context.respond(Messages.MUSIC_NOT_PLAYING);
            return;
        }

        final Player player = PlayerManager.getExisting(context.guild());

        if (!PlayerManager.isInPlayingVoiceChannel(player, context.member()))
        {
            context.respond(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL);
            return;
        }

        long ff = 10;

        if (context.hasArgs())
        {
            if (!StringUtils.isNumeric(context.arg(0)))
            {
                context.respond(Messages.COMMAND_FAST_FORWARD_USAGE);
                return;
            }
            ff = TimeUnit.SECONDS.toMillis(Long.parseLong(context.arg(0)));
        }

        final AudioTrack track = player.getAudioPlayer().getPlayingTrack();
        final long position = track.getPosition();
        final long duration = track.getDuration();
        final long forwarded = position + ff;

        if (duration <= forwarded)
        {
            player.getQueue().playNext();
            return;
        }

        track.setPosition(forwarded);
    }
}