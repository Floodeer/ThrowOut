package com.floodeer.throwout.util;

import com.floodeer.throwout.ThrowOut;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Util {

    private static ThreadLocalRandom random = ThreadLocalRandom.current();

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void connectToServer(Player player, String server) {
        if (!ThrowOut.get().getServer().getPluginManager().isPluginEnabled(ThrowOut.get()))
            return;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(ThrowOut.get(), "BungeeCord", stream.toByteArray());
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
