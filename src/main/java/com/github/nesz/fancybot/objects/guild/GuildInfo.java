package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.translation.Lang;

public class GuildInfo {

    private Lang lang;
    private int volume;

    public GuildInfo(Lang lang, int volume) {
        this.lang = lang;
        this.volume = volume;
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
}