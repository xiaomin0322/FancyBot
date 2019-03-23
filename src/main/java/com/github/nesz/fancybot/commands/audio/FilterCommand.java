package com.github.nesz.fancybot.commands.audio;

import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.github.natanbc.lavadsp.tremolo.TremoloPcmAudioFilter;
import com.github.nesz.fancybot.commands.Command;
import com.github.nesz.fancybot.commands.CommandContext;
import com.github.nesz.fancybot.commands.CommandType;
import com.github.nesz.fancybot.objects.audio.Filter;
import com.github.nesz.fancybot.objects.audio.Player;
import com.github.nesz.fancybot.objects.audio.PlayerManager;
import com.github.nesz.fancybot.objects.translation.Messages;
import com.github.nesz.fancybot.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.filter.ResamplingPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class FilterCommand extends Command
{

    public FilterCommand()
    {
        super("filter", Collections.singletonList("f"), CommandType.PARENT);
    }

    @Override
    public void execute(final CommandContext context)
    {
        if (!context.hasArgs())
        {
            context.respond(Messages.COMMAND_FILTER_USAGE);
            return;
        }

        final Optional<Filter> filterOptional = Arrays.stream(Filter.values())
                .filter(f -> f.name().equalsIgnoreCase(context.arg(0)))
                .findAny();

        if (!filterOptional.isPresent())
        {
            context.respond(Messages.FILTER_NOT_FOUND);
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

        final Filter filter = filterOptional.get();
        switch (filter)
        {

            case SPEED:
            {
                if (context.args().length != 2)
                {
                    context.respond(Messages.COMMAND_FILTER_USAGE);
                    return;
                }

                if (!StringUtils.isNumeric(context.arg(1)))
                {
                    context.respond(Messages.COMMAND_FILTER_USAGE);
                    return;
                }

                final double speed = Double.valueOf(context.arg(1));

                player.getAudioPlayer().setFilterFactory((track, format, output) ->
                {
                    final TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(
                            output,
                            format.channelCount,
                            format.sampleRate
                    );

                    audioFilter.setSpeed(speed);
                    return Collections.singletonList(audioFilter);
                });
                break;
            }

            case VAPORWAVE:
            {
                player.getAudioPlayer().setFilterFactory((track, format, output) ->
                {
                    final TimescalePcmAudioFilter audioFilter = new TimescalePcmAudioFilter(
                            output,
                            format.channelCount,
                            format.sampleRate
                    ).setSpeed(1.0f * 0.5f)
                     .setPitchSemiTones(0.0f - 7.0);

                    return Collections.singletonList(audioFilter);
                });
                break;
            }

            case TREMOLO:
            {
                player.getAudioPlayer().setFilterFactory((track, format, output) ->
                {
                    final TremoloPcmAudioFilter tremolo = new TremoloPcmAudioFilter(
                            output,
                            format.channelCount,
                            format.sampleRate
                    ).setDepth(0.75f);

                    return Collections.singletonList(tremolo);
                });
                break;
            }

            case NIGHTCORE:
            {
                player.getAudioPlayer().setFilterFactory((track, format, output) ->
                {
                    final AudioConfiguration config = PlayerManager.getAudioManager().getConfiguration();

                    final ResamplingPcmAudioFilter nightcore = new ResamplingPcmAudioFilter(
                            config,
                            format.channelCount,
                            output,
                            format.sampleRate,
                            (int) (format.sampleRate / 1.4f)
                    );

                    final TimescalePcmAudioFilter timescalePcmAudioFilter = new TimescalePcmAudioFilter(
                            output,
                            format.channelCount,
                            format.sampleRate
                    ).setSpeed(1.0f);

                    return Arrays.asList(nightcore, timescalePcmAudioFilter);
                });
                break;
            }

            case DISTORTION:
            {
                player.getAudioPlayer().setFilterFactory((track, format, output) ->
                {
                    final DistortionPcmAudioFilter distortion = new DistortionPcmAudioFilter(
                            output,
                            format.channelCount
                    );

                    return Collections.singletonList(distortion);
                });
                break;
            }

            case NONE:
            {
                player.getAudioPlayer().setFilterFactory(null);
                context.respond(context.translate(Messages.FILTER_DISABLED));
                return;
            }
        }

        context.respond(context.translate(Messages.FILTER_APPLIED).replace("{FILTER}", filter.name()));
    }
}