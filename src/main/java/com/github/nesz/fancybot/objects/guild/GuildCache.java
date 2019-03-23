package com.github.nesz.fancybot.objects.guild;

import com.github.nesz.fancybot.objects.database.Queries;
import com.github.nesz.fancybot.objects.translation.Language;

public class GuildCache
{

    private final long guildId;
    private Language language;
    private int volume;
    private boolean notifications;
    private String prefix;
    private boolean autoPlay;

    public GuildCache(final long guildId, final Language language, final int volume, final boolean notifications, final String prefix, final boolean autoPlay)
    {
        this.guildId = guildId;
        this.language = language;
        this.volume = volume;
        this.notifications = notifications;
        this.prefix = prefix;
        this.autoPlay = autoPlay;
    }

    public void save()
    {
        Queries.updateGuild(this);
    }

    public long getGuildId()
    {
        return guildId;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(final Language language)
    {
        this.language = language;
        save();
    }

    public int getVolume()
    {
        return volume;
    }

    public void setVolume(final int volume)
    {
        this.volume = volume;
        save();
    }

    public boolean notifications()
    {
        return notifications;
    }

    public void setNotifications(final boolean notifications)
    {
        this.notifications = notifications;
        save();
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(final String prefix)
    {
        this.prefix = prefix;
        save();
    }

    public boolean isAutoPlay()
    {
        return autoPlay;
    }

    public void setAutoPlay(final boolean autoPlay)
    {
        this.autoPlay = autoPlay;
        save();
    }
}