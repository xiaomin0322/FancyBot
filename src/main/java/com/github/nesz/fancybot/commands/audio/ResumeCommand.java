package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.Arrays;

public class ResumeCommand extends Command
{

    public ResumeCommand()
    {
        super("resume", Arrays.asList("res", "go"), CommandType.PARENT);
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

        if (!player.getAudioPlayer().isPaused())
        {
            return;
        }

        player.getAudioPlayer().setPaused(false);
    }
}