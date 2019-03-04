package com.github.nesz.fancybot.objects.reactions;

public enum Emote {

    PREV(":prev:", "551864551491239983"),
    NEXT(":next:", "551864618247782451");

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

    public String getFullId() {
        return name + id;
    }


}
