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

public class FileUtils {

    public static String pathWithoutJar() {
        try {
            return new File(StringUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (URISyntaxException e) {
            return "";
        }
    }

    public static void saveFile(String file) {
        try {
            String path = pathWithoutJar();

            File out = Paths.get(path + "/" + file).toFile();

            if (out.exists()) {
                return;
            }

            System.out.println(path);

            InputStream is = FancyBot.class.getClassLoader().getResourceAsStream(file);

            if (is == null) {
                FancyBot.LOG.error("Resource file `" + file + "` does not exists");
                return;
            }


            Files.copy(is, Paths.get(path + "/" + file));
        } catch (IOException e) {
            FancyBot.LOG.error("Failed to load resource `" + file + "`", e);
        }
    }

    public static void saveDir(String dir, List<String> files) {
        try {
            String path = pathWithoutJar();
            Path dirPath = Paths.get(path + "/" + dir);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }


            for (String file : files) {
                File f = new File(dirPath.toString() + "/" + file);
                if (f.exists()) {
                    continue;
                }
                InputStream is = FancyBot.class.getClassLoader().getResourceAsStream(dir + "/" + file);
                if (is == null) {
                    FancyBot.LOG.debug("Resource file `" + file + "` does not exists");
                    continue;
                }
                FancyBot.LOG.debug("CREATING " + file);
                Files.copy(is, Paths.get(dirPath.toString() + "/" + file));
            }

        } catch (IOException e) {
            FancyBot.LOG.error(e);
        }
    }

}
