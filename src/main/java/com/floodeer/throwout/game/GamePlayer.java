package com.floodeer.throwout.game;

import com.floodeer.throwout.ThrowOut;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

@Getter
public class GamePlayer {

    private UUID uuid;
    private Player player;
    private String name;

    @Setter
    private int wins, losses, gamesPlayed, kills, deaths, totalHitsReceived, totalHits, balance;

    @Setter
    private boolean inGame, isSpectator;

    @Setter
    private Game game;

    @Setter
    private int knockbackPercentage;

    public GamePlayer(UUID uuid) {
        this.uuid = uuid;
        this.player = ThrowOut.get().getServer().getPlayer(uuid);
        if(player != null)
            this.name = player.getName();
    }


    public GamePlayer(String name) {
        this.name = name;
        this.player = ThrowOut.get().getServer().getPlayer(name);
        if(player != null)
            this.uuid = player.getUniqueId();
    }

    public static GamePlayer get(String name) {
        return ThrowOut.get().getPlayerManager().getPlayer(name);
    }

    public static GamePlayer get(Player player) {
        return ThrowOut.get().getPlayerManager().getPlayer(player.getUniqueId());
    }

    public static GamePlayer get(UUID uuid) {
        return ThrowOut.get().getPlayerManager().getPlayer(uuid);
    }

    public void clearInventory(boolean save) {
        if(save) {
            ThrowOut.get().getInventoryManager().storePlayerData(player);
        }
        getPlayer().getInventory().clear();
        getPlayer().getInventory().setArmorContents(null);
        for (PotionEffect potions : getPlayer().getActivePotionEffects()) {
            getPlayer().removePotionEffect(potions.getType());
        }
        getPlayer().setLevel(0);
        getPlayer().setExp(0);
        getPlayer().setFoodLevel(20);
        getPlayer().setHealth(getPlayer().getMaxHealth());
        getPlayer().updateInventory();
    }

    public void restoreInventory() {
        ThrowOut.get().getInventoryManager().restorePlayerData(player);
    }
}
