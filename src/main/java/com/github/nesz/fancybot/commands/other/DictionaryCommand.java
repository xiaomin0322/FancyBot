package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

public class DictionaryCommand extends AbstractCommand {

    public DictionaryCommand() {
        super("dictionary", Arrays.asList("ub", "dic"), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
        if (args.length < 1) {
            MessagingHelper.sendAsync(textChannel, Messages.COMMAND_DICTIONARY_USAGE.get(lang));
            return;
        }

        String term = String.join(" ", args);
        JSONArray definitions = FancyBot.getDictionaryClient().getDefinitions(term);

        if (definitions == null) {
            MessagingHelper.sendAsync(textChannel, Messages.DICTIONARY_NOTHING_FOUND.get(lang));
            return;
        }

        JSONObject definition = definitions.getJSONObject(0);

        int thumbsUp = definition.getInt("thumbs_up");
        int thumbsDown = definition.getInt("thumbs_down");
        float ratio = thumbsDown * 100 / thumbsUp;

        String rating = "**" + thumbsUp + "** " + Emote.THUMB_UP.asEmote() + " - **" + thumbsDown + "** " + Emote.THUMB_DOWN.asEmote() + " (**" +ratio + "%**)";
        EmbedBuilder embedBuilder = EmbedHelper.basicEmbed(Color.blue, member);
        embedBuilder.setTitle("Dictionary: **" + definition.getString("word") + "**", definition.getString("permalink"));
        embedBuilder.addField("Definition", definition.getString("definition"), false);
        embedBuilder.addField("Author", definition.getString("author"), true);
        embedBuilder.addField("Rating", rating, true);
        embedBuilder.addField("Example", definition.getString("example"), false);

        MessagingHelper.sendAsync(textChannel, embedBuilder);
    }
}