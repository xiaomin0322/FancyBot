package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ShuffleCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "shuffle";
    }

    @Override
    public Set<String> getAliases() {
        return Collections.emptySet();
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Shuffle Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                    "**Description:** Shuffle queue. \n" +
                    "**Usage:** shuffle \n" +
                    "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(lang)).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang)).queue();
            return;
        }

        textChannel.sendMessage(Messages.SHUFFLING_QUEUE.get(lang)).queue(mes -> {
            LinkedBlockingQueue<AudioTrack> queue = player.getQueue();
            ArrayList<AudioTrack> queueTemp;
            Collections.shuffle(queueTemp = new ArrayList<>(queue));
            queue.clear();
            queue.addAll(queueTemp);
            mes.editMessage(Messages.SHUFFLED_QUEUE.get(lang)).queue();
        });
    }
}