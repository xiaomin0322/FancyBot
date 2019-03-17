package com.github.nesz.fancybot.commands.playlist.sub;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.playlist.PlaylistManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class PlaylistCreateCommand extends AbstractCommand {

    public PlaylistCreateCommand() {
        super("create", Collections.emptyList(), Collections.emptyList(), CommandType.SUB);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();

        String name = String.join(" ", args);

        if (name.length() > Config.PLAYLIST_NAME_LENGTH) {
            textChannel.sendMessage("Playlist name cannot be longer than 32 characters").queue();
            return;
        }

        boolean success = PlaylistManager.create(name, member.getUser().getIdLong());
        if (success) {
            textChannel.sendMessage("Playlist '" + name + "' created.").queue();
        }
        else {
            textChannel.sendMessage("Error occurred while creating playlist, try again later").queue();
        }

    }
}