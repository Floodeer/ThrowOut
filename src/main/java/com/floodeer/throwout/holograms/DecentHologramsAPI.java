package com.floodeer.throwout.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class DecentHologramsAPI implements IHologram {

    @Override
    public Object create(String identifier, Location loc, List<String> lines) {
        return DHAPI.createHologram(identifier, loc, lines);
    }

    @Override
    public Object create(String identifier, Location loc, List<String> lines, ItemStack item) {
        Hologram hologram = DHAPI.createHologram(identifier, loc);
        DHAPI.addHologramLine(hologram, item.getType());
        lines.forEach(line -> DHAPI.addHologramLine(hologram, line));
        return hologram;
    }

    @Override
    public Object create(String identifier, Location loc, String line) {
        Hologram hologram = DHAPI.createHologram(identifier, loc);
        DHAPI.addHologramLine(hologram, line);
        return hologram;
    }

    @Override
    public Object create(String identifier, Location loc, String line, ItemStack item) {
        Hologram hologram = DHAPI.createHologram(identifier, loc);
        DHAPI.addHologramLine(hologram, item.getType());
        DHAPI.addHologramLine(hologram, line);
        return hologram;
    }

    @Override
    public void setVisibleTo(Object hologram, List<Player> players) {
        if(!(hologram instanceof Hologram))
            return;

        Hologram oldHolo = (Hologram)hologram;
        String name = oldHolo.getName();
        List<HologramLine> lines = oldHolo.getPage(0).getLines();
        Location loc = oldHolo.getLocation();
        oldHolo.delete();

        Hologram holo = DHAPI.createHologram(name, loc, lines.stream().map(HologramLine::getContent).collect(Collectors.toList()));
        holo.setDefaultVisibleState(false);
        players.forEach(holo::setShowPlayer);
    }

    @Override
    public void setInvisibleTo(Object hologram, List<Player> players) {
        if(!(hologram instanceof Hologram))
            return;

        Hologram oldHolo = (Hologram)hologram;
        String name = oldHolo.getName();
        List<HologramLine> lines = oldHolo.getPage(0).getLines();
        Location loc = oldHolo.getLocation();
        oldHolo.delete();

        Hologram holo = DHAPI.createHologram(name, loc, lines.stream().map(HologramLine::getContent).collect(Collectors.toList()));
        holo.setDefaultVisibleState(true);
        players.forEach(holo::setHidePlayer);
    }

    @Override
    public void delete(Object hologram) {
        if(!(hologram instanceof Hologram))
            return;

        DHAPI.removeHologram(((Hologram) hologram).getName());
    }

    @Override
    public void teleport(Object hologram, Location loc) {
        if(!(hologram instanceof Hologram))
            return;

        DHAPI.moveHologram((((Hologram)hologram).getName()), loc);
    }

    @Override
    public void clearLines(Object hologram) {
        if(!(hologram instanceof Hologram))
            return;

        Hologram holo  = (Hologram)hologram;
        holo.getPages().clear();
    }

    @Override
    public List<Object> getHolograms(World world) {
        return Hologram.getCachedHolograms().stream().filter(cur -> cur.getLocation().getWorld() != null && cur.getLocation().getWorld().equals(world)).collect(Collectors.toList());
    }

    @Override
    public void updateLine(Object hologram, int line, String newText) {
        if(!(hologram instanceof Hologram))
            return;

        Hologram holo  = (Hologram)hologram;
        holo.getPages().get(0).setLine(line, newText);
    }

    @Override
    public void createTempHologram(String text, Location loc, int duration) {
        eu.decentsoftware.holograms.api.DecentHologramsAPI.get().getHologramManager().spawnTemporaryHologramLine(loc, text, duration);
    }
}