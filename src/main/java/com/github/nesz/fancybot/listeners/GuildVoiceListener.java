package com.github.nesz.fancybot.listeners;

import com.github.nesz.fancybot.objects.audio.PlayerManager;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildVoiceListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!PlayerManager.isPlaying(event.getGuild())) {
            return;
        }
        if (PlayerManager.isAlone(event.getChannelLeft())) {
            PlayerManager.stop(PlayerManager.getExisting(event.getGuild()).getTriggerChannel());
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (!PlayerManager.isPlaying(event.getGuild())) {
            return;
        }
        if (PlayerManager.isAlone(event.getChannelLeft())) {
            PlayerManager.stop(PlayerManager.getExisting(event.getGuild()).getTriggerChannel());
        }
    }
}