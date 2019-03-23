package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.config.Constants;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

public class PlayCommand extends Command
{

    public PlayCommand()
    {
        super("play", Arrays.asList("pla", "start"), Arrays.asList(
                Permission.VOICE_CONNECT,
                Permission.VOICE_SPEAK
        ), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_PLAY_USAGE);
            return;
        }

        if (PlayerManager.isPlaying(context.guild()))
        {
            final Player player = PlayerManager.getExisting(context.guild());
            if (player.getQueue().size() >= Constants.MAX_QUEUE_SIZE)
            {
                context.respond(Messages.QUEUE_LIMIT_REACHED);
                return;
            }
        }

        if (!context.member().getVoiceState().inVoiceChannel())
        {
            context.respond(Messages.YOU_HAVE_TO_BE_IN_VOICE_CHANNEL);
            return;
        }

        PlayerManager.loadAndPlay(context);
    }
}