package com.floodeer.throwout.manager;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.game.GamePlayer;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, GamePlayer> onlinePlayers = Maps.newHashMap();

    public PlayerManager() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player.getUniqueId());
        }
    }

    public void addPlayer(UUID uuid) {
        if (!this.onlinePlayers.containsKey(uuid)) {
            final GamePlayer gamePlayer = new GamePlayer(uuid);
            onlinePlayers.put(uuid, gamePlayer);
        }
    }

    public void removePlayer(UUID uuid) {
        updateAsync(getPlayer(uuid));
        onlinePlayers.remove(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public GamePlayer getPlayer(String name) {
        for (GamePlayer gPlayer: onlinePlayers.values()) {
            if (gPlayer.getName().equals(name)) {
                return gPlayer;
            }
        }
        return null;
    }

    public Collection<GamePlayer> getAll() {
        return onlinePlayers.values();
    }

    public void shutdown() {
        getAll().forEach(cur -> ThrowOut.get().getDataStorage().savePlayer(cur));
        onlinePlayers.clear();
    }

    public void restart() {
        shutdown();

        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player.getUniqueId());
        }
    }

    public void updateAll() {
        getAll().forEach(cur -> ThrowOut.get().getDataStorage().savePlayer(cur));
    }

    public void updateAllAsync() {
        getAll().forEach(cur -> ThrowOut.get().getDataStorage().savePlayerAsync(cur));
    }

    public void update(GamePlayer gp) {
       ThrowOut.get().getDataStorage().savePlayer(gp);
    }

    public void updateAsync(GamePlayer gp) {
        ThrowOut.get().getDataStorage().savePlayerAsync(gp);
    }
}