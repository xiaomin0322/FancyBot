package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PlayCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "play";
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

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pla", "start");
    }

    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length < 1) {
            textChannel.sendMessage(getUsage()).queue();
            return;
        }
        if (!member.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage("You're not in a voice channel!").queue();
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