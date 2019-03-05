package com.github.nesz.fancybot.commands.audio.playlist;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.commands.audio.playlist.sub.PlaylistCreateCommand;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.*;

public class PlaylistCommand extends AbstractCommand {

    public PlaylistCommand() {
        super("playlist", new HashSet<>(Collections.singletonList("pl")), Collections.emptySet(), CommandType.MAIN);
    }

    private static final Set<AbstractCommand> SUB_COMMANDS = new HashSet<>(Arrays.asList(
            new PlaylistCreateCommand()
    ));

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_PLAYLIST_USAGE.get(lang)).queue();
            return;
        }

        Optional<AbstractCommand> subCommand = SUB_COMMANDS
                .stream()
                .filter(c -> c.getCommand().equalsIgnoreCase(args[0]) || c.getAliases().contains(args[0]))
                .findFirst();

        if (!subCommand.isPresent()) {
            textChannel.sendMessage(Messages.COMMAND_PLAYLIST_USAGE.get(lang)).queue();
            return;
        }

        subCommand.get().execute(message, Arrays.copyOfRange(args, 1, args.length), textChannel, member);
    }
}