package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.commands.AbstractCommand;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.guild.GuildManager;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.Interactable;
import com.github.nesz.fancybot.objects.reactions.Reaction;
import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import com.github.nesz.fancybot.objects.translation.Lang;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.MessagingHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueCommand extends AbstractCommand implements Interactable<Page> {

    public QueueCommand() {
        super("queue", Arrays.asList("que", "list"),
                Arrays.asList(
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.MESSAGE_EXT_EMOJI
        ), CommandType.MAIN);
    }

    @Override
    public void execute(Message message, String[] args, TextChannel textChannel, Member member) {
        if (!PlayerManager.isPlaying(textChannel)) {
            textChannel.sendMessage(printEmpty(textChannel.getGuild())).queue();
            return;
        }

        Player player = PlayerManager.getExisting(textChannel);
        int maxPage = (int) Math.ceil((double) player.getQueue().size() / (double) ITEMS_PER_PAGE);
        MessagingHelper.sendAsync(textChannel, printQueue(textChannel, EmbedHelper.getFooter(member), player, 1), m -> {
            if (maxPage > 1) {
                ReactionManager.addListener(m, getReactionListener(new Page<>(1, maxPage, textChannel)));
            }
        });

    }

    private MessageEmbed printEmpty(Guild guild) {
        Lang lang = GuildManager.getOrCreate(guild).getLang();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(Messages.QUEUE_FOR_SERVER.get(lang).replace("{SERVER}", guild.getName()), null, guild.getIconUrl());
        eb.setDescription(Messages.QUEUE_EMPTY.get(lang));
        eb.setColor(Color.MAGENTA);
        return eb.build();
    }

    private static final String PUNCTUATION_REGEX = "[.,/#!$%^&*;:{}=\\-_`~()\"\'\\]\\[]";
    private final static int ITEMS_PER_PAGE = 15;

    private MessageEmbed printQueue(TextChannel textChannel, MessageEmbed.Footer footer, Player player, int page) {
        Guild guild = textChannel.getGuild();
        if (player.getQueue().isEmpty() && player.getAudioPlayer().getPlayingTrack() == null) {
            return printEmpty(guild);
        }


        Lang lang = GuildManager.getOrCreate(guild).getLang();
        AudioTrack currentTrack  = player.getAudioPlayer().getPlayingTrack();
        VoiceChannel voice       = guild.getSelfMember().getVoiceState().getChannel();
        List<AudioTrack> queue   = new ArrayList<>(player.getQueue());
        AudioTrackInfo currentTrackInfo = currentTrack.getInfo();

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(footer.getText(), footer.getIconUrl())
                .setAuthor(Messages.QUEUE_FOR_SERVER.get(lang).replace("{SERVER}", guild.getName()), null, guild.getIconUrl());

        long currentTrackTime = currentTrackInfo.length - currentTrack.getPosition();
        long totalTime        = currentTrackTime + queue.stream().mapToLong(value -> value.getInfo().length).sum();

        int start = Math.max(0, (page - 1) * ITEMS_PER_PAGE);
        int end   = Math.min(queue.size() - 1, start + ITEMS_PER_PAGE);

        for (int i = start; i < end; i++) {
            AudioTrackInfo trackInfo = queue.get(i).getInfo();
            String time = trackInfo.isStream ? "∞" : StringUtils.getDurationMinutes(trackInfo.length);
            eb.appendDescription(i+1 + ". " +
                    EmbedHelper.bold(
                    EmbedHelper.asLink(optimizeTitle(trackInfo.title), trackInfo.uri) +
                    " (" + time + ") \n"
            ));
        }

        eb.addField(Messages.MUSIC_NOW_PLAYING.get(lang),
                EmbedHelper.bold(
                EmbedHelper.asLink(optimizeTitle(currentTrackInfo.title), currentTrackInfo.uri) +
                " (" + (currentTrackInfo.isStream ? "∞" : StringUtils.getDurationMinutes(currentTrackTime)) + " left)"
                ), false);

        eb.addField(Messages.QUEUE_TIME.get(lang), "`" + StringUtils.getReadableTime(totalTime) + "`", true);
        eb.addField(Messages.QUEUE_SIZE.get(lang), "`" + queue.size() + " " + Messages.SONGS.get(lang) + "`", true);
        eb.addField(Messages.VOLUME.get(lang), "`" + player.getAudioPlayer().getVolume() + "%`", true);
        eb.addField(Messages.PAUSE.get(lang), "`" + player.getAudioPlayer().isPaused() + "`", true);
        eb.addField(Messages.REPEAT.get(lang), "`" + player.getRepeatMode().name() + "`", true);
        eb.addField(Messages.PLAYING_IN.get(lang), voice == null ? Messages.NO_CHANNEL.get(lang) : "`" + voice.getName() + "`", true);
        return eb.build();
    }

    private String optimizeTitle(String title) {

        return title.substring(0, Math.min(40, title.length())).replaceAll(PUNCTUATION_REGEX, "");
    }

    @Override
    public Reaction<Page> getReactionListener(Page initialData) {
        Reaction<Page> listener = new Reaction<>(initialData);
        listener.registerReaction(Emote.PREV, o -> {
            if (listener.getData().previousPage()) {
                o.editMessage(printQueue(
                        initialData.getTextChannel(),
                        o.getEmbeds().get(0).getFooter(),
                        PlayerManager.getExisting(o.getTextChannel()),
                        listener.getData().getCurrentPage()))
                        .complete();
            }
        });
        listener.registerReaction(Emote.NEXT, o -> {
            if (listener.getData().nextPage()) {
                o.editMessage(printQueue(
                        initialData.getTextChannel(),
                        o.getEmbeds().get(0).getFooter(),
                        PlayerManager.getExisting(o.getTextChannel()),
                        listener.getData().getCurrentPage()))
                        .complete();
            }
        });
        return listener;
    }
}