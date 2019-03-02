package com.github.nesz.fancybot.utils;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Reflections {

    @SuppressWarnings("UnstableApiUsage")
    public static <T> Set<Class<? extends T>> getSubtypesOf(String packageName, Class<?> subtype) throws IOException, ClassNotFoundException {
        Set<Class<? extends T>> set = new HashSet<>();
        ClassPath cp = ClassPath.from(Reflections.class.getClassLoader());
        for (ClassPath.ClassInfo info : cp.getTopLevelClassesRecursive(packageName)) {

            Class clazz = Class.forName(info.getName());

            if (clazz.equals(subtype)) {
                continue;
            }

            if (subtype.isAssignableFrom(clazz)) {
                set.add(clazz);
            }

        }

        return set;
    }
}
