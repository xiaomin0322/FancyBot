package com.github.nesz.fancybot.objects.translation;

public enum Messages {

    YOU_HAVE_TO_BE_IN_VOICE_CHANNEL("youHaveToBeInVoiceChannel"),
    NOT_ENOUGH_PERMISSIONS("notEnoughPermissions"),
    MUSIC_NOW_PLAYING("musicNowPlaying"),
    MUSIC_QUEUED_SONG("musicQueuedSong"),
    MUSIC_NOTHING_FOUND("musicNothingFound"),
    MUSIC_LOADED_PLAYLIST("musicLoadedPlaylist"),
    MUSIC_NOT_PLAYING("musicNotPlaying"),
    MUSIC_CANNOT_LOAD("musicCannotLoad"),
    MUSIC_VOLUME_CHANGED("musicVolumeChanged"),
    YOU_HAVE_TO_BE_IN_MY_VOICE_CHANNEL("musicYouHaveToBeInMyVoiceChannel"),
    LANGUAGE_NOT_FOUND("languageNotFound"),
    LANGUAGE_CHANGED("languageChanged"),
    LYRICS_NOT_FOUND("lyricsNotFound"),
    QUEUE_LIMIT_REACHED("queueLimitReached"),
    QUEUE_THERE_IS_NO_PREVIOUS_SONG("queueThereIsNoPreviousSong"),
    NOTIFICATIONS_TURNED_OFF("notificationsTurnedOff"),
    NOTIFICATIONS_TURNED_ON("notificationsTurnedOn"),
    QUEUE_FOR_SERVER("queueForServer"),
    QUEUE_EMPTY("queueEmpty"),
    QUEUE_TIME("queueTime"),
    QUEUE_SIZE("queueSize"),
    VOLUME("volume"),
    PAUSE("pause"),
    REPEAT("repeat"),
    PLAYING_IN("playingIn"),
    NO_CHANNEL("noChannel"),
    SONGS("songs"),
    INVOKED_BY("invokedBy"),
    SHUFFLING_QUEUE("shufflingQueue"),
    SHUFFLED_QUEUE("shuffledQueue"),
    CHANGED_REPEAT_MODE("changedRepeatMode"),
    INVALID_REPEAT_MODE("invalidRepeatMode"),
    TRACK_WITH_ID_DOES_NOT_EXISTS("trackWithIdDoesNotExists"),
    TRACK_REMOVED_FROM_QUEUE("trackRemovedFromQueue"),
    REDDIT_NOTHING_FOUND("redditNothingFound"),

    COMMAND_PLAYLIST_USAGE("commandPlaylistUsage"),
    COMMAND_FAST_FORWARD_USAGE("commandFastForwardUsage"),
    COMMAND_LYRICS_USAGE("commandLyricsUsage"),
    COMMAND_NOTIFICATIONS_USAGE("commandNotificationsUsage"),
    COMMAND_PAUSE_USAGE("commandPauseUsage"),
    COMMAND_PLAY_USAGE("commandPlayUsage"),
    COMMAND_PREVIOUS_USAGE("commandPreviousUsage"),
    COMMAND_QUEUE_USAGE("commandQueueUsage"),
    COMMAND_REMOVE_USAGE("commandRemoveUsage"),
    COMMAND_REPEAT_USAGE("commandRepeatUsage"),
    COMMAND_RESUME_USAGE("commandResumeUsage"),
    COMMAND_REWIND_USAGE("commandRewindUsage"),
    COMMAND_SHUFFLE_COMMAND("commandShuffleUsage"),
    COMMAND_SKIP_USAGE("commandSKipUsage"),
    COMMAND_STOP_USAGE("commandStopUsage"),
    COMMAND_VOLUME_USAGE("commandVolumeUsage"),
    COMMAND_REDDIT_USAGE("commandRedditUsage"),
    COMMAND_LANG_USAGE("commandLangUsage");


    private String key;

    Messages(String key) {
        this.key = key;
    }

    public String get(Lang lang) {
        return Lang.translate(lang, key);
    }
}
