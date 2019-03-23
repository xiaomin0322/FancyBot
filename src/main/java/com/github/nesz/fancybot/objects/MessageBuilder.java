package com.github.nesz.fancybot.objects;

public class MessageBuilder
{

    private String builder;

    public MessageBuilder(final String message)
    {
        builder = message;
    }

    public MessageBuilder()
    {
        builder = "";
    }

    public MessageBuilder breakLine() {
        builder += "\n";
        return this;
    }

    public MessageBuilder append(final String append) {
        builder += append;
        return this;
    }

    public MessageBuilder with(final String what, final String to)
    {
        builder = builder.replace(what, to);
        return this;
    }

    public MessageBuilder with(final String what, final Object to)
    {
        builder = builder.replace(what, to.toString());
        return this;
    }

    public MessageBuilder appendWith(final String append, final String what, final String to) {
        builder += append.replace(what, to);
        return this;
    }

    public MessageBuilder appendWith(final String append, final String what, final Object to) {
        builder += append.replace(what, to.toString());
        return this;
    }

    public String build()
    {
        return builder;
    }

    @Override
    public String toString()
    {
        return builder;
    }
}
