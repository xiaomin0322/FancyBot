package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.audio.RepeatMode;
import com.github.nesz.fancybot.objects.translation.Messages;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class RepeatCommand extends Command
{

    public RepeatCommand()
    {
        super("repeat", Collections.singletonList("loop"), CommandType.PARENT);
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

        if (!context.hasArgs())
        {
            switch (player.getRepeatMode())
            {
                case NONE:
                    player.setRepeatMode(RepeatMode.TRACK);
                    break;
                case TRACK:
                    player.setRepeatMode(RepeatMode.PLAYLIST);
                    break;
                case PLAYLIST:
                    player.setRepeatMode(RepeatMode.NONE);
                    break;
            }
            context.respond(context.translate(Messages.CHANGED_REPEAT_MODE)
                   .replace("{MODE}", player.getRepeatMode().name()));
            return;
        }

        final Optional<RepeatMode> repeatMode = Arrays.stream(RepeatMode.values())
                .filter(e -> e.name().equalsIgnoreCase(context.arg(0)))
                .findAny();

        if (!repeatMode.isPresent())
        {
            context.respond(Messages.INVALID_REPEAT_MODE);
            return;
        }

        player.setRepeatMode(repeatMode.get());
        context.respond(context.translate(Messages.CHANGED_REPEAT_MODE)
               .replace("{MODE}", player.getRepeatMode().name()));
    }
}