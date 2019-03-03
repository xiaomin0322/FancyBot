package com.github.nesz.fancybot.objects.translation;

public enum Messages {

    YOU_HAVE_TO_BE_IN_VOICE_CHANNEL("youHaveToBeInVoiceChannel"),
    NOT_ENOUGH_PERMISSIONS("notEnoughPermissions"),
    MUSIC_NOW_PLAYING("musicNowPlaying"),
    MUSIC_QUEUED_SONG("musicQueuedSong"),
    MUSIC_NOTHING_FOUND("musicNothingFound"),
    MUSIC_LOADED_PLAYLIST("musicLoadedPlaylist"),
    MUSIC_CANNOT_LOAD("musicCannotLoad");



    private String key;

    Messages(String key) {
        this.key = key;
    }

    public String get(Lang lang) {
        return Lang.translate(lang, key);
    }
}
