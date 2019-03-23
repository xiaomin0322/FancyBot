package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.http.basic.HTTPResponse;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;

public class LyricsCommand extends Command
{

    public LyricsCommand()
    {
        super("lyrics", CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (context.hasArgs())
        {
            final String mes = String.join(" ", context.args());
            context.respond(lyricsEmbed(context, mes));
            return;
        }

        if (!PlayerManager.isPlaying(context.guild()))
        {
            context.respond(Messages.MUSIC_NOT_PLAYING);
            return;
        }

        final Player player = PlayerManager.getExisting(context.guild());

        if (!PlayerManager.isInPlayingVoiceChannel(player, context.member()))
        {
            context.respond(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL);
            return;
        }

        final String title = normalize(player.getAudioPlayer().getPlayingTrack().getInfo().title);
        context.respond(lyricsEmbed(context, title));
    }

    private static MessageEmbed lyricsEmbed(final CommandContext context, final String query)
    {
        final EmbedBuilder embed = EmbedHelper.basicEmbed(Color.ORANGE, context);
        final HTTPResponse<JSONObject> response = FancyBot.getGeniusClient().getTopSearch(query);

        if (!response.getData().isPresent())
        {
            embed.setDescription(context.translate(Messages.LYRICS_NOT_FOUND));
            return embed.build();
        }

        final JSONObject responseData = response.getData().get();
        final HTTPResponse<String> responseLyrics = FancyBot.getGeniusClient().getLyrics(responseData.getString("url"));

        if (!responseLyrics.getData().isPresent())
        {
            embed.setDescription(context.translate(Messages.LYRICS_NOT_FOUND));
            return embed.build();
        }

        final String lyrics = responseLyrics.getData().get()
                .replaceAll("\\[(.*?)]", "**[$1]**");

        embed.setTitle(responseData.getString("full_title"), responseData.getString("url"));
        embed.setThumbnail(responseData.getString("song_art_image_thumbnail_url"));
        embed.setDescription(lyrics.substring(0, Math.min(lyrics.length(), MessageEmbed.TEXT_MAX_LENGTH - embed.length())));
        embed.appendDescription("\n...");
        return embed.build();
    }

    private static String normalize(final String query)
    {
        return query
                .replaceAll("\\[(.*?)]", "")
                .replaceAll("\\((.*?)\\)", "")
                .replaceAll("(?i)download", "")
                .replaceAll("(?i)official", "")
                .replaceAll("(?i)audio", "")
                .replaceAll("(?i)video", "")
                .replace("\"", "")
                .replace("[]", "")
                .replace("()", "")
                .trim();
    }
}