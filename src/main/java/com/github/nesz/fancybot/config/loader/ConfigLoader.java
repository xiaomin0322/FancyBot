package com.github.nesz.fancybot.config.loader;

import com.github.nesz.fancybot.FancyBot;
import com.github.nesz.fancybot.config.Config;
import com.github.nesz.fancybot.utils.FileUtils;
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

public class ConfigLoader
{

    private final Class configClass;
    private final String file;
    private final Set<Field> fields;

    public ConfigLoader(final Class configClass, final String file)
    {
        this.configClass = configClass;
        this.file = file;
        this.fields = Arrays.stream(Config.class.getDeclaredFields())
                .filter(f -> !Modifier.isPrivate(f.getModifiers()))
                .filter(f -> !Modifier.isFinal(f.getModifiers()))
                .collect(Collectors.toSet());
    }

    public void override()
    {
        try
        {
            final String path = FileUtils.executionPath() + "/" + file;
            String json = "";

            try (final BufferedReader br = Files.newBufferedReader(Paths.get(path)))
            {
                json += br.lines().collect(Collectors.joining());
            }
            catch (final IOException e)
            {
                FancyBot.LOGGER.error("Error occurred while accessing config file", e);
                System.exit(1);
            }

            final JSONObject jo = new JSONObject(json);

            final Object temp = configClass.newInstance();
            for (final Field field : fields)
            {
                field.set(temp, jo.getString(field.getName()));
            }
        }
        catch (final InstantiationException | IllegalAccessException e)
        {
            FancyBot.LOGGER.error("Error occurred while overriding field", e);
            System.exit(1);
        }
    }
}
