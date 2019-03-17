package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Collections;

public class RedditCommand extends AbstractCommand {

    public RedditCommand() {
        super("reddit", Collections.singletonList("r"), Collections.emptyList(), CommandType.MAIN);
    }
    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_REDDIT_USAGE.get(lang));
            return;
        }

        String subreddit = String.join("_", args);
        String link = FancyBot.getImgurClient().randomRedditImage(subreddit);

        if (link == null) {
            MessagingHelper.sendAsync(textChannel, Messages.REDDIT_NOTHING_FOUND.get(lang));
            return;
        }

        MessagingHelper.sendAsync(textChannel, link);
    }
}
