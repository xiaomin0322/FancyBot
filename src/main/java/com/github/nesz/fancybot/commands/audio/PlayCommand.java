package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class PlayCommand extends AbstractCommand {

    public PlayCommand() {
        super("play", new HashSet<>(Arrays.asList("pla", "start")), Collections.emptySet(), CommandType.MAIN);
    }

    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_PLAY_USAGE.get(lang)).queue();
            return;
        }

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