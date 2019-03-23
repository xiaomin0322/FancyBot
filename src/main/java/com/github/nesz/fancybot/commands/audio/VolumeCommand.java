package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;

import java.util.Collections;

public class VolumeCommand extends Command
{

    public VolumeCommand()
    {
        super("volume", Collections.singletonList("vol"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_VOLUME_USAGE);
            return;
        }

        if (!StringUtils.isNumeric(context.arg(0)))
        {
            context.respond(Messages.COMMAND_VOLUME_USAGE);
            return;
        }

        int volume = Integer.parseInt(context.arg(0));

        if (volume < 0)
        {
            volume = 0;
        }

        context.guildCache().setVolume(volume);
        context.respond(context.translate(Messages.MUSIC_VOLUME_CHANGED)
               .replace("{VOLUME}", String.valueOf(volume)));

        if (PlayerManager.isPlaying(context.guild()))
        {
            final Player player = PlayerManager.getExisting(context.guild());
            player.getAudioPlayer().setVolume(volume);
        }
    }
}