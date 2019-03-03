package com.github.nesz.fancybot.objects.translation;

import com.github.nesz.fancybot.FancyBot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public enum Lang {

    POLISH("pl_PL", fileToMap("pl_PL.properties")),
    ENGLISH("en_US", fileToMap("en_US.properties"));

    private String locale;
    private Map<String, String> translation;

    Lang(String locale, Map<String, String> translation) {
        this.locale = locale;
        this.translation = translation;
    }

    public String getLocale() {
        return locale;
    }

    public static String translate(Lang lang, String key) {
        return lang.translation.get(key);
    }

    private static Map<String, String> fileToMap(String file) {
        Properties properties = new Properties();
        String propFileName = "lang/" + file;

        try (InputStream inputStream = Lang.class.getClassLoader().getResourceAsStream(propFileName)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        }
        catch (IOException e) {
            FancyBot.LOG.error("Could not load property file", e);
        }

        return (Map) properties;
    }
}
