package com.github.nesz.fancybot.objects.translation;

public enum Messages
{

    YOU_HAVE_TO_BE_SERVER_OWNER("youHaveToBeServerOwner"),
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
    PREFIX_CANNOT_BE_LONGER_THAN_4_CHARS("prefixCannotBeLongerThan4Chars"),
    AUTO_PLAY_TURNED_ON("autoPlayTurnedOn"),
    AUTO_PLAY_TURNED_OFF("autoPlayTurnedOff"),
    DICTIONARY_NOTHING_FOUND("dictionaryNothingFound"),
    TRACK_MOVE_INVALID_POSITION("trackMoveInvalidPosition"),
    TRACK_MOVE_SAME_POSITIONS("trackMoveSamePositions"),
    TRACK_MOVED("trackMoved"),
    FILTER_APPLIED("filterApplied"),
    FILTER_DISABLED("filterDisabled"),
    FILTER_NOT_FOUND("filterNotFound"),
    PEWDIEPIE_COMMAND_RATE_LIMITED("pewdiepieRateLimited"),
    PEWDIEPIE_COMMAND_ERROR("pewdiepieError"),
    SELF_MENTION_MESSAGE("selfMentionMessage"),


    COMMAND_PLAYLIST_USAGE("commandPlaylistUsage"),
    COMMAND_FAST_FORWARD_USAGE("commandFastForwardUsage"),
    COMMAND_NOTIFICATIONS_USAGE("commandNotificationsUsage"),
    COMMAND_PLAY_USAGE("commandPlayUsage"),
    COMMAND_REMOVE_USAGE("commandRemoveUsage"),
    COMMAND_REWIND_USAGE("commandRewindUsage"),
    COMMAND_SKIP_USAGE("commandSkipUsage"),
    COMMAND_VOLUME_USAGE("commandVolumeUsage"),
    COMMAND_REDDIT_USAGE("commandRedditUsage"),
    COMMAND_LANG_USAGE("commandLangUsage"),
    COMMAND_PREFIX_USAGE("commandPrefixUsage"),
    COMMAND_AUTO_PLAY_USAGE("commandAutoPlayUsage"),
    COMMAND_DICTIONARY_USAGE("commandDictionaryUsage"),
    COMMAND_MOVE_USAGE("commandMoveUsage"),
    COMMAND_FILTER_USAGE("commandFilterUsage");


    private final String key;

    Messages(final String key)
    {
        this.key = key;
    }

    public String get(final Language language)
    {
        return Language.translate(language, key);
    }
}
