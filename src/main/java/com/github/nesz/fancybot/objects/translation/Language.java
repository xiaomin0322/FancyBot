package com.github.nesz.fancybot.objects.translation;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public enum Language
{

    POLISH("pl_PL", fileToMap("pl_PL.properties")),
    ENGLISH("en_US", fileToMap("en_US.properties"));

    private final String locale;
    private final Properties translation;

    Language(final String locale, final Properties translation)
    {
        this.locale = locale;
        this.translation = translation;
    }

    public String getLocale()
    {
        return locale;
    }

    public static String translate(final Language language, final String key)
    {
        final String translated = language.translation.getProperty(key);

        if (translated == null)
        {
            return "Missing translation for key `" + key + "`";
        }

        return translated;
    }

    private static Properties fileToMap(final String file)
    {
        final Properties properties = new Properties();
        final String filePath = FileUtils.executionPath() + "/lang/" + file;

        try (final BufferedReader br = Files.newBufferedReader(Paths.get(filePath)))
        {
            properties.load(br);
        }
        catch (final IOException e)
        {
            FancyBot.LOGGER.error("Could not load property file", e);
        }

        return properties;
    }
}
