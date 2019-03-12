package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.translation.Lang;

public class GuildInfo {

    private final long guildId;
    private Lang lang;
    private int volume;
    private boolean notifications;

    public GuildInfo(long guildId, Lang lang, int volume, boolean notifications) {
        this.guildId = guildId;
        this.lang = lang;
        this.volume = volume;
        this.notifications = notifications;
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
}