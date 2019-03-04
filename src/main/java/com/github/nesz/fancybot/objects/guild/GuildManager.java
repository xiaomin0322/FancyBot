package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.translation.Lang;

import java.util.HashMap;
import java.util.Map;

public class GuildManager {

    private static final Map<Long, GuildInfo> GUILD_INFO = new HashMap<>();

    public static GuildInfo getOrCreate(Long guildID) {
        return GUILD_INFO.computeIfAbsent(guildID, v -> new GuildInfo(Lang.ENGLISH, 100, true));
    }

    public static void remove(Long guildID) {
        GUILD_INFO.remove(guildID);
    }
}
