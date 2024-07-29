package com.floodeer.throwout;

import com.floodeer.throwout.config.ConfigOptions;
import com.floodeer.throwout.config.MessagesConfig;
import com.floodeer.throwout.game.Game;
import com.floodeer.throwout.manager.GameManager;
import com.floodeer.throwout.manager.InventoryManager;
import com.floodeer.throwout.manager.PlayerManager;
import com.floodeer.throwout.util.update.Updater;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ThrowOut extends JavaPlugin {

    private static ThrowOut main;

    @Getter
    private ServerVersion version;

    @Getter
    private MessagesConfig language;
    @Getter
    private ConfigOptions configOptions;

    @Getter
    private GameManager gameManager;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private PlayerManager playerManager;

    public static ThrowOut get() {
        return main;
    }

    @Override
    public void onEnable() {
        main = this;

        getLogger().info("Loading files & configurations");

        configOptions = new ConfigOptions(new File(getDataFolder(), "options.yml"));
        try {
            configOptions.load();
        }catch(InvalidConfigurationException ex) {
            ex.printStackTrace();
        }

        language = new MessagesConfig(new File(getDataFolder(), "language.yml"));
        try {
            language.load();
        }catch(InvalidConfigurationException ex) {
            ex.printStackTrace();
        }
        getLogger().info("Loading NMS support...");

        String nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
        getLogger().info("Detected NMS version: " + nmsver);
        version = ServerVersion.getFromString(nmsver);
        getLogger().info("Loaded support for " + version.getNMSIdentifier());

        getLogger().info("Loading managers");
        this.gameManager = new GameManager();
        this.inventoryManager = new InventoryManager();
        this.playerManager = new PlayerManager();

        getLogger().info("Loading updater");
        new Updater(this);
    }

    @Override
    public void onDisable() {
        gameManager.shutdownGames();
        playerManager.shutdown();
    }
}
