package com.github.nesz.fancybot.http.basic;

import java.util.Optional;

public class HTTPResponse<T>
{

    private final int responseCode;
    private final T data;

    public HTTPResponse(final int responseCode, final T data)
    {
        this.responseCode = responseCode;
        this.data = data;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public Optional<T> getData()
    {
        return Optional.ofNullable(data);
    }

}