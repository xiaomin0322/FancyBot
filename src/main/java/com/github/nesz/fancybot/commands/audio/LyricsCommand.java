package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import org.json.JSONObject;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LyricsCommand extends AbstractCommand {

    @Override
    public String getCommand() {
        return "lyrics";
    }

    @Override
    public Set<String> getAliases() {
        return Collections.emptySet();
    }

    @Override
    public Set<Permission> getRequiredPermissions() {
        return new HashSet<>(Collections.singletonList(Permission.MESSAGE_EMBED_LINKS));
    }

    @Override
    public MessageEmbed getUsage() {
        return new EmbedBuilder()
                .setAuthor(":: Lyrics Command ::", null, null)
                .setColor(Color.PINK)
                .setDescription(
                        "**Description:** Searches for lyrics. \n" +
                        "**Usage:** lyrics [TITLE] \n" +
                        "**Aliases:** " + getAliases().toString())
                .build();
    }


    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (args.length > 1) {
            String mes = String.join(" ", args);
            textChannel.sendMessage(lyricsEmbed(textChannel.getGuild(), mes)).queue();
        }
        else {
            Lang lang = GuildManager.getOrCreate(textChannel.getGuild()).getLang();
            if (!PlayerManager.isPlaying(textChannel)) {
                textChannel.sendMessage(Messages.MUSIC_NOT_PLAYING.get(lang)).queue();
                return;
            }

            Player player = PlayerManager.getExisting(textChannel);
            if (!member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() != player.getVoiceChannel()) {
                textChannel.sendMessage(Messages.YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL.get(lang)).queue();
                return;
            }

            String title = normalize(player.getAudioPlayer().getPlayingTrack().getInfo().title);
            textChannel.sendMessage(lyricsEmbed(textChannel.getGuild(), title)).queue();
        }
    }

    private static MessageEmbed lyricsEmbed(Guild guild, String query) {
        EmbedBuilder embed    = new EmbedBuilder().setColor(Color.ORANGE);
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