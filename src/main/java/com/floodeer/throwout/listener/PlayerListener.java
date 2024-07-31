package com.floodeer.throwout.listener;

import com.floodeer.throwout.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(GamePlayer.get(e.getPlayer()).isInGame())
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockBreakEvent e) {
        if(GamePlayer.get(e.getPlayer()).isInGame())
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(FoodLevelChangeEvent e) {
        if(e.getEntity() instanceof Player && GamePlayer.get(e.getEntity().getUniqueId()).isInGame())
            e.setCancelled(true);
    }
}
