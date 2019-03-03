package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VolumeCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "volume";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Collections.singletonList("vol"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Volume Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Changes volume. \n" +
                        "**Usage:** volume [VOLUME] \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length < 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        if (!StringUtils.isNumeric(args[0])) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        GuildInfo guildInfo = GuildManager.getOrCreate(textChannel.getGuild().getIdLong());
        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(guildInfo.getLang())).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(guildInfo.getLang())).queue();
            return;
        }

        int volume = Integer.parseInt(args[0]);
        if (volume < 0) {
            volume = 0;
        }

        textChannel.sendMessage(Messages.MUSIC_VOLUME_CHANGED.get(guildInfo.getLang()).replace("{VOLUME}", String.valueOf(volume))).queue();
        player.getAudioPlayer().setVolume(volume);
        player.setVolume(volume);
    }
}