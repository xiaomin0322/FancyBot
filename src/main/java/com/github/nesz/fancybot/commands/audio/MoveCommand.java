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

public class MoveCommand extends AbstractCommand {

    public MoveCommand() {
        super("move", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length != 2 || !StringUtils.isNumeric(args[0]) || !StringUtils.isNumeric(args[1])) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_MOVE_USAGE.get(lang));
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

        int which = Integer.valueOf(args[0]) - 1;
        int where = Integer.valueOf(args[1]) - 1;

        if (which > player.getQueue().size() || where > player.getQueue().size() || which < 0 || where < 0) {
            MessagingHelper.sendAsync(textChannel, Messages.TRACK_MOVE_INVALID_POSITION.get(lang));
            return;
        }

        if (which == where) {
            MessagingHelper.sendAsync(textChannel, Messages.TRACK_MOVE_SAME_POSITIONS.get(lang));
            return;
        }

        AudioTrack whichTrack = player.getQueue().remove(which);
        AudioTrack whereTrack = player.getQueue().remove(where);

        player.getQueue().add(where, whichTrack);
        player.getQueue().add(which, whereTrack);


        MessagingHelper.sendAsync(textChannel, Messages.TRACK_MOVED.get(lang));
    }
}