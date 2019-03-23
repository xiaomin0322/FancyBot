package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;

public class RemoveCommand extends Command
{

    public RemoveCommand()
    {
        super("remove", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_REMOVE_USAGE);
            return;
        }

        if (!StringUtils.isNumeric(context.arg(0)))
        {
            context.respond(Messages.COMMAND_REMOVE_USAGE);
            return;
        }

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

        final ArrayList<AudioTrack> tracks = new ArrayList<>(player.getQueue().getTracks());
        final int remove = Integer.valueOf(context.arg(0));

        if (tracks.size() < remove)
        {
            context.respond(Messages.TRACK_WITH_ID_DOES_NOT_EXISTS);
            return;
        }

        final String title = tracks.get(remove - 1).getInfo().title;

        for (final AudioTrack track : player.getQueue().getTracks())
        {
            if (track.getInfo().title.equals(title))
            {
                player.getQueue().getTracks().remove(track);
            }
        }

        context.respond(context.translate(Messages.TRACK_REMOVED_FROM_QUEUE)
               .replace("{TITLE}", title).replace("{ID}", String.valueOf(remove)));
    }
}