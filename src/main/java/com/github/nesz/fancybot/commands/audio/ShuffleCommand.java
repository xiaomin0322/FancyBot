package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class ShuffleCommand extends AbstractCommand {

    public ShuffleCommand() {
        super("shuffle", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (!PlayerManager.isPlaying(textChannel)) {
            MessagingHelper.sendAsync(textChannel, Messages.MUSIC_NOT_PLAYING.get(lang));
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang));
            return;
        }

        MessagingHelper.sendAsync(textChannel, Messages.SHUFFLING_QUEUE.get(lang), m -> {
            LinkedBlockingQueue<AudioTrack> queue = player.getQueue();
            ArrayList<AudioTrack> queueTemp;
            Collections.shuffle(queueTemp = new ArrayList<>(queue));
            queue.clear();
            queue.addAll(queueTemp);
            m.editMessage(Messages.SHUFFLED_QUEUE.get(lang)).queue();
        });

        /*textChannel.sendMessage(Messages.SHUFFLING_QUEUE.get(lang)).queue(mes -> {
            LinkedBlockingQueue<AudioTrack> queue = player.getQueue();
            ArrayList<AudioTrack> queueTemp;
            Collections.shuffle(queueTemp = new ArrayList<>(queue));
            queue.clear();
            queue.addAll(queueTemp);
            mes.editMessage(Messages.SHUFFLED_QUEUE.get(lang)).queue();
        });*/
    }
}