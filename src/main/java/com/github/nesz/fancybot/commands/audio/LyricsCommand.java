package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.MessagingHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.json.JSONObject;

import java.awt.*;
import java.util.Collections;

public class LyricsCommand extends AbstractCommand {

    public LyricsCommand() {
        super("lyrics", Collections.emptyList(), Collections.emptyList(), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length > 1) {
            String mes = String.join(" ", args);
            MessagingHelper.sendAsync(textChannel, lyricsEmbed(member, textChannel.getGuild(), mes));
        }
        else {
            Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
            if (!PlayerManager.isPlaying(textChannel)) {
                MessagingHelper.sendAsync(textChannel, Messages.MUSIC_NOT_PLAYING.get(lang));
                return;
            }

            Player player = PlayerManager.getExisting(textChannel);
            if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
                MessagingHelper.sendAsync(textChannel, Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang));
                return;
            }

            String title = normalize(player.getAudioPlayer().getPlayingTrack().getInfo().title);
            MessagingHelper.sendAsync(textChannel, lyricsEmbed(member, textChannel.getGuild(), title));
        }
    }

    private static MessageEmbed lyricsEmbed(Member invoker, Guild guild, String query) {
        EmbedBuilder embed    = EmbedHelper.basicEmbed(Color.ORANGE, invoker);
        JSONObject searchData = FancyBot.getGeniusClient().getTopSearch(query);
        if (searchData == null) {
            Lang lang = GuildManager.getOrCreate(guild).getLang();
            embed.setDescription(Messages.LYRICS_NOT_FOUND.get(lang));
            return embed.build();
        }

        String lyrics = FancyBot.getGeniusClient().getLyrics(searchData.getString("url"));
        if (lyrics == null) {
            Lang lang = GuildManager.getOrCreate(guild).getLang();
            embed.setDescription(Messages.LYRICS_NOT_FOUND.get(lang));
            return embed.build();
        }

        lyrics = lyrics.replaceAll("\\[(.*?)]", "**[$1]**");

        embed.setTitle(searchData.getString("full_title"), searchData.getString("url"));
        embed.setThumbnail(searchData.getString("song_art_image_thumbnail_url"));
        embed.setDescription(lyrics.substring(0, 2043 - embed.length()));
        embed.appendDescription("\n...");
        return embed.build();
    }

    private static String normalize(String query) {
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