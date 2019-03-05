package com.github.nesz.fancybot.commands.audio.playlist.sub;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collections;

public class PlaylistCreateCommand extends AbstractCommand {

    public PlaylistCreateCommand() {
        super("create", Collections.emptySet(), Collections.emptySet(), CommandType.SUB);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_PLAYLIST_USAGE.get(lang)).queue();
            return;
        }

    }
}