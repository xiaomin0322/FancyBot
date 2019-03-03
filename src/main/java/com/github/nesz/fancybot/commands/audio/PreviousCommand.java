package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildInfo;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Messages;
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

public class PreviousCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "previous";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Collections.singletonList("prev"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return Collections.emptySet();
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Previous Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** plays previous track \n" +
                        "**Usage:** previous \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
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

        if (player.getPrevious().isEmpty()) {
            textChannel.sendMessage(Messages.QUEUE_THERE_IS_NO_PREVIOUS_SONG.get(guildInfo.getLang())).queue();
            return;
        }

        player.previousTrack();
    }
}