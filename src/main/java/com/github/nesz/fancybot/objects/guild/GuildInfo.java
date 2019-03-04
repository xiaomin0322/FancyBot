package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.translation.Lang;

public class GuildInfo {

    private Lang lang;
    private int volume;
    private boolean notifications;

    public GuildInfo(Lang lang, int volume, boolean notifications) {
        this.lang = lang;
        this.volume = volume;
        this.notifications = notifications;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean notifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
}