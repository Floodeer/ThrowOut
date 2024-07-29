package com.floodeer.throwout.manager;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private Map<String, ItemStack[]> plinv = new HashMap<>();
    private Map<String, ItemStack[]> plarmor = new HashMap<>();
    private Map<String, Collection<PotionEffect>> pleffects = new HashMap<>();
    private Map<String, Integer> plhunger = new HashMap<>();
    private Map<String, GameMode> plgamemode = new HashMap<>();
    private Map<String, Integer> pllevel = new HashMap<>();
    private Map<String, Float> plexp = new HashMap<>();
    private Map<String, Double> plhealth = new HashMap<>();
    private Map<String, Boolean> plflight = new HashMap<>();
    private Map<String, Location> pllocation = new HashMap<>();

    private void storePlayerInventory(Player player) {
        PlayerInventory pinv = player.getInventory();
        plinv.put(player.getName(), pinv.getContents());
        pinv.clear();
    }

    private void storePlayerFlight(Player player) {
        plflight.put(player.getName(), player.getAllowFlight());
    }

    private void storePlayerArmor(Player player) {
        PlayerInventory pinv = player.getInventory();
        plarmor.put(player.getName(), pinv.getArmorContents());
        pinv.setArmorContents(null);
    }

    private void storePlayerPotionEffects(Player player) {
        Collection<PotionEffect> peff = player.getActivePotionEffects();
        pleffects.put(player.getName(), peff);
        for (PotionEffect peffect : peff) {
            player.removePotionEffect(peffect.getType());
        }
    }

    private void storePlayerHunger(Player player) {
        plhunger.put(player.getName(), player.getFoodLevel());
        player.setFoodLevel(20);
    }

    private void storePlayerGameMode(Player player) {
        plgamemode.put(player.getName(), player.getGameMode());
        player.setGameMode(GameMode.ADVENTURE);
    }

    private void storePlayerLevel(Player player) {
        pllevel.put(player.getName(), player.getLevel());
        player.setLevel(0);
    }

    private void storePlayerExp(Player player) {
        plexp.put(player.getName(), player.getExp());
        player.setExp(0);
    }

    private void storePlayerHealth(Player player) {
        plhealth.put(player.getName(), player.getHealth());
    }

    private void storePlayerLocation(Player player) {
        pllocation.put(player.getName(), player.getLocation());
    }

    private void restorePlayerInventory(Player player) {
        player.getInventory().setContents(plinv.remove(player.getName()));
    }

    private void restorePlayerArmor(Player player) {
        player.getInventory().setArmorContents(plarmor.remove(player.getName()));
    }

    private void restorePlayerPotionEffects(Player player) {
        for (PotionEffect peffect : player.getActivePotionEffects()) {
            player.removePotionEffect(peffect.getType());
        }
        player.addPotionEffects(pleffects.remove(player.getName()));
    }

    private void restorePlayerLocation(Player player) {
        player.teleport(pllocation.remove(player.getName()));
    }


    private void restorePlayerFlight(Player player) {
        player.setAllowFlight(plflight.remove(player.getName()));
    }

    private void restorePlayerHunger(Player player) {
        player.setFoodLevel(plhunger.remove(player.getName()));
    }

    private void restorePlayerExp(Player player) {
        player.setExp(plexp.remove(player.getName()));
    }

    private void restorePlayerGameMode(Player player) {
        player.setGameMode(plgamemode.remove(player.getName()));
    }

    private void restorePlayerLevel(Player player) {
        player.setLevel(pllevel.remove(player.getName()));
    }

    private void restorePlayerHealth(Player player) {
        player.setHealth(plhealth.remove(player.getName()));
    }

    public void storePlayerData(Player player) {
        storePlayerGameMode(player);
        storePlayerFlight(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        storePlayerLevel(player);
        storePlayerInventory(player);
        storePlayerArmor(player);
        storePlayerPotionEffects(player);
        storePlayerHunger(player);
        storePlayerHealth(player);
        storePlayerExp(player);
        storePlayerLocation(player);
        player.updateInventory();
    }

    public void restorePlayerData(Player player) {
        restorePlayerHealth(player);
        restorePlayerHunger(player);
        restorePlayerPotionEffects(player);
        restorePlayerArmor(player);
        restorePlayerInventory(player);
        restorePlayerLevel(player);
        restorePlayerExp(player);
        restorePlayerFlight(player);
        restorePlayerGameMode(player);
        restorePlayerLocation(player);

        player.updateInventory();
    }
}