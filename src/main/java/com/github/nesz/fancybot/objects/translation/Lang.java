package com.github.nesz.fancybot.objects.translation;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        String translated = lang.translation.get(key);
        if (translated == null) {
            return "Missing translation for key `" + key + "`";
        }
        return translated;
    }

    private static Map<String, String> fileToMap(String file) {
        Properties properties = new Properties();

        String filePath = FileUtils.pathWithoutJar() + "/lang/" + file;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            properties.load(br);
        } catch (IOException e) {
            FancyBot.LOG.error("Could not load property file", e);
        }

        return (Map) properties;
    }
}
