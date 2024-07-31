package com.floodeer.throwout;

import com.floodeer.throwout.config.ConfigOptions;
import com.floodeer.throwout.config.MessagesConfig;
import com.floodeer.throwout.manager.GameManager;
import com.floodeer.throwout.manager.InventoryManager;
import com.floodeer.throwout.manager.PlayerManager;
import com.floodeer.throwout.storage.DataStorage;
import com.floodeer.throwout.storage.Database;
import com.floodeer.throwout.storage.MySQLDatabase;
import com.floodeer.throwout.storage.SQLiteDatabase;
import com.floodeer.throwout.util.update.Updater;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
    @Getter
    private DataStorage dataStorage;

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

        getLogger().info("Loading database");
        try {
            setupDatabase();
        } catch (SQLException | IOException | ClassNotFoundException e) {
            getLogger().severe("Error while loading database.");
            this.setEnabled(false);
            e.printStackTrace();
            return;
        }

        getLogger().info("Loading updater");
        new Updater(this);
    }

    @Override
    public void onDisable() {
        gameManager.shutdownGames();
        playerManager.shutdown();
    }

    private void setupDatabase() throws SQLException, ClassNotFoundException, IOException {
        String driver = getConfigOptions().databaseDriver;
        Database db;
        if(driver.equals("org.sqlite.JDBC") || driver.equalsIgnoreCase("org.h2.Driver")) {
            db = new SQLiteDatabase(getConfigOptions().dbDatabase);
        }else{
            db = new MySQLDatabase(getConfigOptions().dbHost, getConfigOptions().dbDatabase, getConfigOptions().dbUser, getConfigOptions().dbPassword, getConfigOptions().dbPort);
        }
        db.createTables();

        dataStorage = new DataStorage(db);
    }
}
