package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;

public class RemoveCommand extends AbstractCommand {

    public RemoveCommand() {
        super("remove", Collections.emptySet(), Collections.emptySet(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_REMOVE_USAGE.get(lang)).queue();
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            textChannel.sendMessage(Messages.COMMAND_REMOVE_USAGE.get(lang)).queue();
            return;
        }

        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(lang)).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang)).queue();
            return;
        }

        ArrayList<AudioTrack> tracks = new ArrayList<>(player.getQueue());
        int remove = Integer.parseInt(args[0]);
        if (tracks.size() < remove) {
            textChannel.sendMessage(Messages.TRACK_WITH_ID_DOES_NOT_EXISTS.get(lang)).queue();
            return;
        }

        String title = tracks.get(remove - 1).getInfo().title;
        for (AudioTrack track : player.getQueue()) {
            if (track.getInfo().title.equals(title)) {
                player.getQueue().remove(track);
            }
        }

        textChannel.sendMessage(Messages.TRACK_REMOVED_FROM_QUEUE.get(lang).replace("{TITLE}", title).replace("{ID}", String.valueOf(remove))).queue();
    }
}