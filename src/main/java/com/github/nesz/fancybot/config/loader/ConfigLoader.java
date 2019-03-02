package com.github.nesz.fancybot.config.loader;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.config.Config;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigLoader {

    private final Class configClass;
    private final String file;
    private final Set<Field> fields;

    public ConfigLoader(Class configClass, String file) {
        this.configClass = configClass;
        this.file = file;
        this.fields = Arrays.stream(Config.class.getDeclaredFields())
                .filter(f -> !Modifier.isPrivate(f.getModifiers()))
                .filter(f -> !Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toSet());
    }

    public void override() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = classLoader.getResource("config.json").getPath();
        String json = "";

        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            json += br.lines().collect(Collectors.joining());
        } catch (IOException e) {
            FancyBot.LOG.debug("Error occurred while accessing config file", e);
        }

        JSONObject jo = new JSONObject(json);

        Config temp = new Config();
        fields.forEach(field -> {
            try {
                field.set(temp, jo.getString(field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
