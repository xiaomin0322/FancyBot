package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.translation.Lang;

public class GuildInfo {

    private final long guildId;
    private Lang lang;
    private int volume;
    private boolean notifications;
    private String prefix;
    private boolean autoPlay;

    public GuildInfo(long guildId, Lang lang, int volume, boolean notifications, String prefix, boolean autoPlay) {
        this.guildId = guildId;
        this.lang = lang;
        this.volume = volume;
        this.notifications = notifications;
        this.prefix = prefix;
        this.autoPlay = autoPlay;
    }

    public void save() {
        Queries.updateGuild(this);
    }

    public long getGuildId() {
        return guildId;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
        save();
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        save();
    }

    public boolean notifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
        save();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        save();
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        save();
    }
}