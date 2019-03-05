package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.audio.RepeatMode;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class RepeatCommand extends AbstractCommand {

    public RepeatCommand() {
        super("repeat", new HashSet<>(Collections.singletonList("loop")), Collections.emptySet(), CommandType.MAIN);
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

        if (args.length < 1) {
            switch (player.getRepeatMode()) {
                case NONE:
                    player.setRepeatMode(RepeatMode.TRACK);
                    break;
                case TRACK:
                    player.setRepeatMode(RepeatMode.PLAYLIST);
                    break;
                case PLAYLIST:
                    player.setRepeatMode(RepeatMode.NONE);
                    break;
            }
            textChannel.sendMessage(Messages.CHANGED_REPEAT_MODE.get(lang).replace("{MODE}", player.getRepeatMode().name())).queue();
        }
        else {
            RepeatMode repeatMode = Arrays.stream(RepeatMode.values()).filter(e -> e.name().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (repeatMode == null) {
                textChannel.sendMessage(Messages.INVALID_REPEAT_MODE.get(lang)).queue();
                return;
            }

            player.setRepeatMode(repeatMode);
                textChannel.sendMessage(Messages.CHANGED_REPEAT_MODE.get(lang).replace("{MODE}", player.getRepeatMode().name())).queue();
        }
    }
}