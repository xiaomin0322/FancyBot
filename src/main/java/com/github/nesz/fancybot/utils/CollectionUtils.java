package com.github.nesz.fancybot.utils;

import java.util.Collections;
import java.util.List;

public class CollectionUtils
{

    public static <T> List<T> safeSubList(final List<T> list, int fromIndex, int toIndex)
    {
        final int size = list.size();

        if (fromIndex >= size || toIndex <= 0 || fromIndex >= toIndex)
        {
            return Collections.emptyList();
        }

        fromIndex = Math.max(0, fromIndex);
        toIndex = Math.min(size, toIndex);

        return list.subList(fromIndex, toIndex);
    }

}
