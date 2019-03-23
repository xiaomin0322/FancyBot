package com.github.nesz.fancybot.utils;

import com.github.nesz.fancybot.FancyBot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils
{

    public static String executionPath()
    {
        try
        {
            return new File(StringUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        }
        catch (final URISyntaxException e)
        {
            return "";
        }
    }

    public static void saveFile(final String file)
    {
        try
        {
            final String path = executionPath();

            final File out = Paths.get(path + "/" + file).toFile();

            if (out.exists())
            {
                return;
            }

            final InputStream is = FancyBot.class.getClassLoader().getResourceAsStream(file);

            if (is == null)
            {
                FancyBot.LOGGER.error("Resource file `" + file + "` does not exists");
                return;
            }


            Files.copy(is, Paths.get(path + "/" + file));
        }
        catch (final IOException e)
        {
            FancyBot.LOGGER.error("Failed to load resource `" + file + "`", e);
        }
    }

    public static void saveDir(final String dir, final List<String> files)
    {
        try
        {
            final String path = executionPath();
            final Path dirPath = Paths.get(path + "/" + dir);

            if (!Files.exists(dirPath))
            {
                Files.createDirectory(dirPath);
            }


            for (final String file : files)
            {

                final File f = new File(dirPath.toString() + "/" + file);

                if (f.exists())
                {
                    continue;
                }

                final InputStream is = FancyBot.class.getClassLoader().getResourceAsStream(dir + "/" + file);

                if (is == null)
                {
                    FancyBot.LOGGER.error("Resource file `" + file + "` does not exists");
                    continue;
                }

                Files.copy(is, Paths.get(dirPath.toString() + "/" + file));
            }

        }
        catch (final IOException e)
        {
            FancyBot.LOGGER.error(e);
        }
    }

}
