package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public Set<String> getAliases() {
        return new HashSet<>(Arrays.asList("pla", "start"));
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return new HashSet<>(Arrays.asList(Permission.MESSAGE_EMBED_LINKS));
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Play Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Plays music. \n" +
                        "**Usage:** play [NAME/URL]    \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }

    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length < 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }

        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (PlayerManager.isPlaying(textChannel)) {
            Player player = PlayerManager.getExisting(textChannel);
            if (player.getQueue().size() >= PlayerManager.MAX_QUEUE_SIZE) {
                textChannel.sendMessage(Messages.QUEUE_LIMIT_REACHED.get(lang)).queue();
                return;
            }
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_VOICE_CHANNEL.get(lang)).queue();
            return;
        }

        String track = String.join(" ", args);

        if (track.startsWith("http") || track.startsWith("www")) {
            PlayerManager.loadAndPlay(textChannel, track, member, false);
            return;
        }

        String link = YOUTUBE_BASE + FancyBot.getYouTubeClient().getFirstID(track);
        PlayerManager.loadAndPlay(textChannel, link, member, false);
    }
}