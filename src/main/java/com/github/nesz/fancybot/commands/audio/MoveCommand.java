package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MoveCommand extends Command
{

    public MoveCommand()
    {
        super("move", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (context.args().length != 2 || !StringUtils.isNumeric(context.arg(0)) || !StringUtils.isNumeric(context.arg(1)))
        {
            context.respond(Messages.COMMAND_MOVE_USAGE);
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

        final int which = Integer.valueOf(context.arg(0)) - 1;
        final int where = Integer.valueOf(context.arg(1)) - 1;

        if (which > player.getQueue().size() || where > player.getQueue().size() || which < 0 || where < 0)
        {
            context.respond(Messages.TRACK_MOVE_INVALID_POSITION);
            return;
        }

        if (which == where)
        {
            context.respond(Messages.TRACK_MOVE_SAME_POSITIONS);
            return;
        }

        final AudioTrack whichTrack = player.getQueue().getTracks().remove(which);
        final AudioTrack whereTrack = player.getQueue().getTracks().remove(where);

        player.getQueue().getTracks().add(where, whichTrack);
        player.getQueue().getTracks().add(which, whereTrack);


        context.respond(Messages.TRACK_MOVED);
    }
}