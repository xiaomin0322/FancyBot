package com.github.nesz.fancybot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@SuppressWarnings("unchecked")
public class Reflections
{

    public static <T> Set<Class<? extends T>> getSubtypesOf(final String packageName, final Class<?> subtype)
    {
        final Set<Class<? extends T>> set = new HashSet<>();
        for (final Class clazz : getClassesInPackage(packageName))
        {
            if (clazz.equals(subtype))
            {
                continue;
            }
            if (subtype.isAssignableFrom(clazz))
            {
                set.add(clazz);
            }
        }
        return set;
    }

    private static List<Class<?>> getClassesInPackage(final String packageName)
    {
        final String path = packageName.replaceAll("\\.", File.separator);
        final List<Class<?>> classes = new ArrayList<>();
        final String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (final String classpathEntry : classPathEntries)
        {
            if (classpathEntry.endsWith(".jar"))
            {
                final File jar = new File(classpathEntry);
                try
                {
                    final JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while ((entry = is.getNextJarEntry()) != null)
                    {
                        name = entry.getName();
                        if (name.endsWith(".class"))
                        {
                            if (name.contains(path) && name.endsWith(".class"))
                            {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                }
                catch (final Exception ex)
                {
                    // Silence is gold
                }
            }
            else
            {
                try
                {
                    final File base = new File(classpathEntry + File.separatorChar + path);
                    for (final File file : base.listFiles())
                    {
                        name = file.getName();
                        if (name.endsWith(".class"))
                        {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                }
                catch (final Exception ex)
                {
                    // Silence is gold
                }
            }
        }

        return classes;
    }
}
