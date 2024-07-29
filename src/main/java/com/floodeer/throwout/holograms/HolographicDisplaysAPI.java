package com.floodeer.throwout.holograms;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.util.Util;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class HolographicDisplaysAPI implements IHologram {

    @Override
    public Hologram create(String identifier, Location loc, List<String> lines) {
        Hologram holo = HologramsAPI.createHologram(ThrowOut.get(), loc);
        lines.forEach(cur -> holo.appendTextLine(Util.color(cur)));
        return holo;
    }

    @Override
    public Hologram create(String identifier, Location loc, List<String> lines, ItemStack item) {
        Hologram holo = HologramsAPI.createHologram(ThrowOut.get(), loc);
        lines.forEach(cur -> holo.appendTextLine(Util.color(cur)));
        holo.appendItemLine(item);
        return holo;
    }

    @Override
    public Object create(String identifier, Location loc, String line) {
        Hologram holo = HologramsAPI.createHologram(ThrowOut.get(), loc);
        holo.appendTextLine(Util.color(line));
        return holo;
    }

    @Override
    public Object create(String identifier, Location loc, String line, ItemStack item) {
        Hologram holo = HologramsAPI.createHologram(ThrowOut.get(), loc);
        holo.appendTextLine(Util.color(line));
        return holo;
    }

    @Override
    public void setVisibleTo(Object hologram, List<Player> players) {
        Hologram holo = (Hologram)hologram;
        VisibilityManager vm = holo.getVisibilityManager();
        players.forEach(vm::showTo);
    }

    @Override
    public void setInvisibleTo(Object hologram, List<Player> players) {
        Hologram holo = (Hologram)hologram;
        VisibilityManager vm = holo.getVisibilityManager();
        players.forEach(vm::hideTo);
    }


    @Override
    public void delete(Object hologram) {
        if(!(hologram instanceof Hologram))
            return;

        ((Hologram)hologram).delete();
    }

    @Override
    public void teleport(Object hologram, Location loc) {
        if(!(hologram instanceof Hologram))
            return;

        ((Hologram)hologram).teleport(loc);
    }

    @Override
    public void clearLines(Object hologram) {
        if(!(hologram instanceof Hologram))
            return;

        ((Hologram)hologram).clearLines();
    }

    @Override
    public List<Object> getHolograms(World world) {
        return HologramsAPI.getHolograms(ThrowOut.get()).stream().filter(hologram -> hologram.getWorld() != null && hologram.getWorld().equals(world)).collect(Collectors.toList());
    }

    @Override
    public void updateLine(Object hologram, int line, String newText) {
        if(!(hologram instanceof Hologram))
            return;

        ((Hologram) hologram).removeLine(line);
        ((Hologram)hologram).insertTextLine(line, newText);
    }

    @Override
    public void createTempHologram(String text, Location loc, int duration) {
        Hologram hd = (Hologram) create(null, loc, text);
        ThrowOut.get().getServer().getScheduler().runTaskLater(ThrowOut.get(), () -> {
            hd.delete();
        }, duration);
    }
}