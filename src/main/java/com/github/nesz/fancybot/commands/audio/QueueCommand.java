package com.github.nesz.fancybot.commands.audio;

import com.github.nesz.fancybot.objects.MessageBuilder;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.pagination.Page;
import com.github.nesz.fancybot.objects.reactions.Emote;
import com.github.nesz.fancybot.objects.reactions.Interactable;
import com.github.nesz.fancybot.objects.reactions.Reaction;
import com.github.nesz.fancybot.objects.reactions.ReactionManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.EmbedHelper;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueCommand extends Command implements Interactable<Page>
{

    public QueueCommand()
    {
        super("queue", Arrays.asList("que", "list"), Arrays.asList(
                Permission.MESSAGE_EMBED_LINKS,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_EXT_EMOJI
        ), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!PlayerManager.isPlaying(context.guild()))
        {
            context.respond(printEmpty(context));
            return;
        }

        final Player player = PlayerManager.getExisting(context.guild());
        final int maxPage = (int) Math.ceil((double) player.getQueue().size() / (double) ITEMS_PER_PAGE);
        context.respond(printQueue(context, player, 1), message ->
        {
            if (maxPage > 1) ReactionManager.addListener(message, getReactionListener(new Page<>(1, maxPage, context)));
        });

    }

    private MessageEmbed printEmpty(final CommandContext context)
    {
        final EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(context.translate(Messages.QUEUE_FOR_SERVER)
                .replace("{SERVER}", context.guild().getName()), null, context.guild().getIconUrl())
                .setDescription(context.translate(Messages.QUEUE_EMPTY))
                .setColor(Color.MAGENTA);
        return eb.build();
    }

    private static final String PUNCTUATION_REGEX = "[.,/#!$%^&*;:{}=\\-_`~()\"\'\\]\\[|]";
    private final static int ITEMS_PER_PAGE = 15;

    private MessageEmbed printQueue(final CommandContext context, final Player player, final int page)
    {
        if (player.getQueue().getTracks().isEmpty() && player.getAudioPlayer().getPlayingTrack() == null)
        {
            return printEmpty(context);
        }

        final AudioTrack currentTrack   = player.getAudioPlayer().getPlayingTrack();
        final VoiceChannel voiceChannel = context.guild().getSelfMember().getVoiceState().getChannel();
        final List<AudioTrack> queue    = new ArrayList<>(player.getQueue().getTracks());
        final AudioTrackInfo currTrackInfo = currentTrack.getInfo();

        final EmbedBuilder eb = EmbedHelper.basicEmbed(Color.RED, context)
                .setAuthor(context.translate(Messages.QUEUE_FOR_SERVER)
                .replace("{SERVER}", context.guild().getName()), null, context.guild().getIconUrl());

        final long currentTrackTime = currTrackInfo.length - currentTrack.getPosition();
        final long totalTime = currentTrackTime + queue.stream().mapToLong(value -> value.getInfo().length).sum();

        final int start = Math.max(0, (page - 1) * ITEMS_PER_PAGE);
        final int end = Math.min(queue.size() - 1, start + ITEMS_PER_PAGE);

        for (int i = start; i <= end; i++)
        {
            final AudioTrackInfo trackInfo = queue.get(i).getInfo();
            final String message = new MessageBuilder("{INDEX}. **[{TITLE}]({URI}) ({TIME})**\n")
                    .with("{INDEX}", i+1)
                    .with("{TITLE}", normalize(trackInfo.title))
                    .with("{URI}", trackInfo.uri)
                    .with("{TIME}", trackInfo.isStream ? "∞" : StringUtils.getDurationMinutes(trackInfo.length))
                    .build();

            eb.appendDescription(message);
        }

        final String message = new MessageBuilder("**[{TITLE}]({URI}) ({TIME})**\n")
                .with("{TITLE}", normalize(currTrackInfo.title))
                .with("{URI}", currTrackInfo.uri)
                .with("{TIME}", currTrackInfo.isStream ? "∞" :
                        StringUtils.getDurationMinutes(currentTrack.getInfo().length - currentTrack.getPosition()) + " left")
                .build();

        eb.addField(context.translate(Messages.MUSIC_NOW_PLAYING), message, false);
        eb.addField(context.translate(Messages.QUEUE_TIME), "`" + StringUtils.getReadableTime(totalTime) + "`", true);
        eb.addField(context.translate(Messages.QUEUE_SIZE), "`" + queue.size() + " " + context.translate(Messages.SONGS) + "`", true);
        eb.addField(context.translate(Messages.VOLUME), "`" + player.getAudioPlayer().getVolume() + "%`", true);
        eb.addField(context.translate(Messages.PAUSE), "`" + player.getAudioPlayer().isPaused() + "`", true);
        eb.addField(context.translate(Messages.REPEAT), "`" + player.getRepeatMode().name() + "`", true);
        eb.addField(context.translate(Messages.PLAYING_IN), voiceChannel == null ? context.translate(Messages.NO_CHANNEL) : "`" + voiceChannel.getName() + "`", true);
        return eb.build();
    }

    private String normalize(final String title)
    {
        return title.substring(0, Math.min(40, title.length())).replaceAll(PUNCTUATION_REGEX, "");
    }

    @Override
    public Reaction<Page> getReactionListener(final Page initialData)
    {
        final Reaction<Page> listener = new Reaction<>(initialData);

        listener.registerReaction(Emote.PREV, o ->
        {
            if (listener.getData().previousPage())
            {
                o.editMessage(printQueue(
                        (CommandContext) initialData.getExtra(),
                        PlayerManager.getExisting(o.getTextChannel()),
                        listener.getData().getCurrentPage())
                ).queue();
            }
        });

        listener.registerReaction(Emote.NEXT, o ->
        {
            if (listener.getData().nextPage())
            {
                o.editMessage(printQueue(
                        (CommandContext) initialData.getExtra(),
                        PlayerManager.getExisting(o.getTextChannel()),
                        listener.getData().getCurrentPage())
                ).queue();
            }
        });

        return listener;
    }
}