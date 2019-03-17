package com.github.nesz.fancybot.objects.reactions;

public enum Emote {

    PREV(":prev:", "551864551491239983"),
    NEXT(":next:", "551864618247782451"),
    PLAY(":play:", "552067440083730443"),
    THUMB_UP(":thumb_up:", "556647053296664607"),
    THUMB_DOWN(":thumb_up:", "556646998292299806");

    private final String name;
    private final String id;

    Emote(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String asSnowflake() {
        return name + id;
    }

    public String asEmote() {
        return "<" + asSnowflake() + ">";
    }


}
