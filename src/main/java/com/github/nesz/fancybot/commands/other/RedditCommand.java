package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.RandomUtil;
import org.json.JSONArray;

import java.util.Collections;

public class RedditCommand extends Command
{

    public RedditCommand()
    {
        super("reddit", Collections.singletonList("r"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_REDDIT_USAGE);
            return;
        }

        final String subreddit = String.join("_", context.args());
        final HTTPResponse<JSONArray> response = FancyBot.getImgurClient()
                .retrieveRedditImages(subreddit);

        if (!response.getData().isPresent())
        {
            context.respond(Messages.REDDIT_NOTHING_FOUND);
            return;
        }

        final JSONArray data = response.getData().get();
        final int random = RandomUtil.getRandomIntBetween(0, data.length());
        final String link = data.getJSONObject(random)
                .getString("link");

        context.respond(link);
    }
}
