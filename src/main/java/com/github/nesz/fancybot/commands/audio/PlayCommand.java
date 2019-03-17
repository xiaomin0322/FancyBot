package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;

public class PlayCommand extends AbstractCommand {

    public PlayCommand() {
        super("play", Arrays.asList("pla", "start"), Collections.emptyList(), CommandType.MAIN);
    }

    private static final String YOUTUBE_BASE = "https://www.youtube.com/watch?v=";

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_PLAY_USAGE.get(lang));
            return;
        }

        if (PlayerManager.isPlaying(textChannel)) {
            Player player = PlayerManager.getExisting(textChannel);
            if (player.getQueue().size() >= PlayerManager.MAX_QUEUE_SIZE) {
                MessagingHelper.sendAsync(textChannel, Messages.QUEUE_LIMIT_REACHED.get(lang));
                return;
            }
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_IN_VOICE_CHANNEL.get(lang));
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