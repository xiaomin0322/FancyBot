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

import java.util.ArrayList;
import java.util.Collections;

public class RemoveCommand extends AbstractCommand {

    public RemoveCommand() {
        super("remove", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_REMOVE_USAGE.get(lang));
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_REMOVE_USAGE.get(lang));
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

        ArrayList<AudioTrack> tracks = new ArrayList<>(player.getQueue());
        int remove = Integer.parseInt(args[0]);
        if (tracks.size() < remove) {
            MessagingHelper.sendAsync(textChannel, Messages.TRACK_WITH_ID_DOES_NOT_EXISTS.get(lang));
            return;
        }

        String title = tracks.get(remove - 1).getInfo().title;
        for (AudioTrack track : player.getQueue()) {
            if (track.getInfo().title.equals(title)) {
                player.getQueue().remove(track);
            }
        }

        String msg = Messages.TRACK_REMOVED_FROM_QUEUE.get(lang).replace("{TITLE}", title).replace("{ID}", String.valueOf(remove));
        MessagingHelper.sendAsync(textChannel, msg);

    }
}