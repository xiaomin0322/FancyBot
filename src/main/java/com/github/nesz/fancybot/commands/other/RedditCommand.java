package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Collections;
import java.util.HashSet;

public class RedditCommand extends AbstractCommand {

    public RedditCommand() {
        super("reddit", new HashSet<>(Collections.singletonList("r")), Collections.emptySet(), CommandType.MAIN);
    }
    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            textChannel.sendMessage(Messages.COMMAND_REDDIT_USAGE.get(lang)).queue();
            return;
        }

        String subreddit = String.join("_", args);
        String link = FancyBot.getImgurClient().randomRedditImage(subreddit);

        if (link == null) {
            textChannel.sendMessage(Messages.REDDIT_NOTHING_FOUND.get(lang)).queue();
            return;
        }

        textChannel.sendMessage(link).queue();
    }
}
