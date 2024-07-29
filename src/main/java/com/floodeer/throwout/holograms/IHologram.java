package com.floodeer.throwout.holograms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IHologram {

    Object create(String identifier, Location loc, List<String> lines);

    Object create(String identifier, Location loc, List<String> lines, ItemStack item);

    Object create(String identifier, Location loc, String line);

    Object create(String identifier,Location loc, String line, ItemStack item);

    void setVisibleTo(Object hologram, List<Player> players);

    void setInvisibleTo(Object hologram, List<Player> players);

    void delete(Object hologram);

    void teleport(Object hologram, Location loc);

    void clearLines(Object hologram);

    List<Object> getHolograms(World world);

    void updateLine(Object hologram, int line, String newText);

    void createTempHologram(String text, Location loc, int duration);
}