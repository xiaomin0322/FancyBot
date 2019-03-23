package com.github.nesz.fancybot.commands.other;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.RandomUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PewdiepieCommand extends Command
{
    public PewdiepieCommand()
    {
        super("pewdiepie", Arrays.asList("pdp", "pew", "subgap", "tseries"), CommandType.PARENT);
    }

    private static final int RATE_LIMITED = 403;

    private static final String PEWDIEPIE_CHANNEL_ID = "UC-lHJZR3Gqxm24_Vd_AJ5Yw";
    private static final String TSERIES_CHANNEL_ID = "UCq-Fj5jknLsUf-MWSy4_brA";

    @Override
    public void execute(final CommandContext context)
    {
        final HTTPResponse<JSONArray> response = FancyBot.getYouTubeClient()
                .retrieveDataForChannels(Arrays.asList(PEWDIEPIE_CHANNEL_ID, TSERIES_CHANNEL_ID));

        if (response.getResponseCode() == RATE_LIMITED)
        {
            context.respond(Messages.PEWDIEPIE_COMMAND_RATE_LIMITED);
            return;
        }

        if (!response.getData().isPresent())
        {
            context.respond(Messages.PEWDIEPIE_COMMAND_ERROR);
            return;
        }

        final JSONArray data = response.getData().get();
        final JSONObject tseries;
        final JSONObject pewdiepie;

        if (data.getJSONObject(0).getString("id").equals(PEWDIEPIE_CHANNEL_ID))
        {
            pewdiepie = data.getJSONObject(0).getJSONObject("statistics");
            tseries = data.getJSONObject(1).getJSONObject("statistics");
        }
        else
        {
            pewdiepie = data.getJSONObject(1).getJSONObject("statistics");
            tseries = data.getJSONObject(0).getJSONObject("statistics");
        }

        final int pewdiepieSubs = Integer.valueOf(pewdiepie.getString("subscriberCount"));
        final int tseriesSubs = Integer.valueOf(tseries.getString("subscriberCount"));
        final int subGap = Math.abs(pewdiepieSubs - tseriesSubs);
        final boolean pdpLeading = pewdiepieSubs > tseriesSubs;

        final DecimalFormat formatter = new DecimalFormat("###,###,###");

        final EmbedBuilder eb = EmbedHelper.basicEmbed(RandomUtil.getRandomColor(), context);
        eb.addField("PewDiePie", formatter.format(pewdiepieSubs), true);
        eb.addField("T-Series", formatter.format(tseriesSubs), true);
        eb.addField("Who is leading?",  String.format("__**%s**__ is leading with **%s** subscribers",
                pdpLeading ? "PewDiePie" : "T-Series", formatter.format(subGap)), false);

        context.respond(eb);
    }
}
