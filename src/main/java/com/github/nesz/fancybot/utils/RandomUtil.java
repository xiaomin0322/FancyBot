package com.github.nesz.fancybot.utils;

import java.awt.*;
import java.util.Random;

public class RandomUtil
{

    private static final Random rand = new Random();

    public static int getRandomIntBetween(final int min, final int max) throws IllegalArgumentException
    {
        return rand.nextInt(max - min + 1) + min;
    }

    public static Double getRandomDoubleBetween(final double min, final double max) throws IllegalArgumentException
    {
        return (rand.nextDouble() * (max - min)) + min;
    }

    public static Float getRandomFloat() throws IllegalArgumentException
    {
        return rand.nextFloat();
    }

    public static boolean getChance(final double chance)
    {
        return (chance >= 100) || (chance >= getRandomDoubleBetween(0, 100));
    }

    public static Color getRandomColor()
    {
        return new Color(
                getRandomIntBetween(0, 255),
                getRandomIntBetween(0, 255),
                getRandomIntBetween(0, 255)
        );
    }
}
