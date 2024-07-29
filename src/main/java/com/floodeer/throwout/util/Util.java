package com.floodeer.throwout.util;

import org.bukkit.ChatColor;

import java.util.concurrent.ThreadLocalRandom;

public class Util {

    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String createSpacer() {
        String build = "";

        for (int i = 0; i < 15; i++) {
            build = add(build);
        }

        return build;

    }

    private static String add(String build) {

        int r = random.nextInt(7) + 1;

        build = build + ChatColor.values()[r];

        return build;
    }
}
