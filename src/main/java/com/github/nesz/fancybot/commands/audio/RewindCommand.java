package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RewindCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "rewind";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Arrays.asList("re", "r"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Rewind Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** skips music x seconds backwards. \n" +
                        "**Usage:** re [SECONDS] \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length != 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild().getIdLong());
        if (!StringUtils.isNumeric(args[0])) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(guildInfo.getLang())).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(guildInfo.getLang())).queue();
            return;
        }

        AudioTrack track = player.getAudioPlayer().getPlayingTrack();
        long ff = TimeUnit.SECONDS.toMillis(Long.parseLong(args[0]));
        long position = track.getPosition();
        long rewinded = position - ff;
        if (rewinded <= 0) {
            if (player.getPrevious() == null) {
                track.setPosition(0);
                return;
            }
            player.previousTrack();
            return;
        }
        track.setPosition(rewinded);
    }
}