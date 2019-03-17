package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.audio.RepeatMode;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Arrays;
import java.util.Collections;

public class RepeatCommand extends AbstractCommand {

    public RepeatCommand() {
        super("repeat", Collections.singletonList("loop"), Collections.emptyList(), CommandType.MAIN);
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
            MessagingHelper.sendAsync(textChannel, Messages.CHANGED_REPEAT_MODE.get(lang).replace("{MODE}", player.getRepeatMode().name()));

        }
        else {
            RepeatMode repeatMode = Arrays.stream(RepeatMode.values()).filter(e -> e.name().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (repeatMode == null) {
                MessagingHelper.sendAsync(textChannel, Messages.INVALID_REPEAT_MODE.get(lang));
                return;
            }

            player.setRepeatMode(repeatMode);
            MessagingHelper.sendAsync(textChannel, Messages.CHANGED_REPEAT_MODE.get(lang).replace("{MODE}", player.getRepeatMode().name()));
        }
    }
}