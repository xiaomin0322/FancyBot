package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.objects.audio.PlayerManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildVoiceListener extends ListenerAdapter
{

    @Override
    public void onGuildVoiceLeave(final GuildVoiceLeaveEvent event)
    {
        if (!PlayerManager.isPlaying(event.getGuild()))
        {
            return;
        }

        if (PlayerManager.isAlone(event.getChannelLeft()))
        {
            PlayerManager.stop(event.getGuild());
        }
    }

    @Override
    public void onGuildVoiceMove(final GuildVoiceMoveEvent event)
    {
        if (!PlayerManager.isPlaying(event.getGuild()))
        {
            return;
        }

        if (PlayerManager.isAlone(event.getChannelLeft()))
        {
            PlayerManager.stop(event.getGuild());
        }
    }
}