package com.github.nesz.fancybot.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Reflections {

    public static <T> Set<Class<? extends T>> getSubtypesOf(String packageName, Class<?> subtype) throws IOException, ClassNotFoundException {
        Set<Class<? extends T>> set = new HashSet<>();
        for (Class clazz : getClasses(packageName)) {
            if (clazz.equals(subtype)) {
                continue;
            }
            if (subtype.isAssignableFrom(clazz)) {
                set.add(clazz);
            }
        }
        return set;
    }

    private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
