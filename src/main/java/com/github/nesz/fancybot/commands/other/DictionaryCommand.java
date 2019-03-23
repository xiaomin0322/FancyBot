package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;

public class DictionaryCommand extends Command
{

    public DictionaryCommand()
    {
        super("dictionary", Arrays.asList("ub", "dic"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_DICTIONARY_USAGE);
            return;
        }

        final String term = String.join(" ", context.args());
        final HTTPResponse<JSONArray> response = FancyBot.getDictionaryClient()
                .retriveDefinitions(term);

        if (!response.getData().isPresent())
        {
            context.respond(Messages.DICTIONARY_NOTHING_FOUND);
            return;
        }

        final JSONObject definition = response.getData().get().getJSONObject(0);

        final int thumbsUp = definition.getInt("thumbs_up");
        final int thumbsDown = definition.getInt("thumbs_down");
        final int thumbsTotal = thumbsUp + thumbsDown;
        final int ratio = (thumbsUp / thumbsTotal * 100);

        final String rating = new MessageBuilder("**{UP}** {EMOTE_UP} - **{DOWN}** {EMOTE_DOWN} (**{RATIO}%**)")
                .with("{UP}", thumbsUp)
                .with("{EMOTE_UP}", Emote.THUMB_UP.asEmote())
                .with("{DOWN}", thumbsDown)
                .with("{EMOTE_DOWN}", Emote.THUMB_DOWN.asEmote())
                .with("{RATIO}", ratio)
                .build();

        final EmbedBuilder builder = EmbedHelper.basicEmbed(Color.blue, context);
        builder.setTitle("Dictionary: **" + definition.getString("word") + "**", definition.getString("permalink"));
        builder.addField("Definition", definition.getString("definition"), false);
        builder.addField("Author", definition.getString("author"), true);
        builder.addField("Rating", rating, true);
        builder.addField("Example", definition.getString("example"), false);

        context.respond(builder);
    }
}