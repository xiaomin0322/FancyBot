package com.github.nesz.fancybot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{

    public static boolean isNumeric(final String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isUUID(final String check)
    {
        if (check.length() != 36)
        {
            return false;
        }

        return UUID_PATTERN.matcher(check).matches();
    }

    public static String getReadableTime(final long millis)
    {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String getDurationMinutes(final long length)
    {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }

    private static final Pattern INPUT_PATTERN = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

    public static String[] inputWithQuotes(final String input)
    {
        final List<String> matchList = new ArrayList<>();
        final Matcher regexMatcher = INPUT_PATTERN.matcher(input);
        while (regexMatcher.find())
        {
            if (regexMatcher.group(1) != null)
            {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            }
            else if (regexMatcher.group(2) != null)
            {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            }
            else
            {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }
        return matchList.toArray(new String[matchList.size()]);
    }


    /*

        COPIED FROM APACHE COMMONS

     */

    private static final int INDEX_NOT_FOUND = -1;

    public static String substringBetween(final String str, final String open, final String close)
    {
        if (str == null || open == null || close == null)
        {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND)
        {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND)
            {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
}
