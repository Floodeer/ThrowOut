package com.floodeer.throwout.game;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.util.GameDataFile;
import com.floodeer.throwout.util.GameDataYaml;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GameArena {

    @Getter
    private final String map;

    private GameDataFile gameFile;

    public GameArena(String map) {
        this.map = map;
        if(doesMapExists()) {
            gameFile = GameDataYaml.getMap(map);
        }

    }

    public boolean doesMapExists() {
        File mapsFolder = new File(ThrowOut.get().getDataFolder(), "maps");
        if (!mapsFolder.exists() || !mapsFolder.isDirectory()) {
            return false;
        }

        File[] files = mapsFolder.listFiles();
        if (files == null) {
            return false;
        }

        String targetMapName = this.map.toLowerCase();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.toLowerCase().endsWith(".yml") &&
                    fileName.substring(0, fileName.length() - 4).equalsIgnoreCase(targetMapName)) {
                return true;
            }
        }
        return false;
    }

    public void deleteArena() throws IOException {
        if (getGameFile().exists()) {
            getGameFile().delete();
        }
    }

    public File getGameFile() throws FileNotFoundException {
        File mapsFolder = new File(ThrowOut.get().getDataFolder(), "maps");
        if (!mapsFolder.exists() || !mapsFolder.isDirectory()) {
            throw new FileNotFoundException("Maps folder not found or not a directory.");
        }

        File[] files = mapsFolder.listFiles();
        if (files == null) {
            throw new FileNotFoundException("Failed to list files in maps folder.");
        }

        String targetMapName = this.map.toLowerCase();
        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".yml")) {
                String mapName = fileName.substring(0, fileName.length() - 4);
                if (mapName.equalsIgnoreCase(targetMapName)) {
                    return file;
                }
            }
        }

        throw new FileNotFoundException("Map file not found: " + this.map);
    }

    public int getMinPlayers() {
        return 	gameFile.getInteger("Map.MinPlayers");
    }

    public int getMaxPlayers() {
        return 	gameFile.getInteger("Map.MaxPlayers");
    }

    public boolean isSignSet() {
        return gameFile.getBoolean("Map.isSignSet");
    }

    public void updateSign() {

    }

    public enum LocationType {
        SPECTATOR_SPAWN("SPECTATOR_SPAWN"), LOBBY("LOBBY"), POWERUP_LOCATION("POWERUP_LOCATION");

        String type;

        LocationType(String str) {
            this.type = str;
        }

        @Override
        public String toString() {
            return type;
        }

        public static String toString(LocationType type) {
            return type.toString();
        }

        public static LocationType fromString(String str) {
            for (LocationType types : LocationType.values()) {
                if (types.toString().equalsIgnoreCase(str)) {
                    return types;
                }
            }
            return null;
        }
    }

    public @NotNull Location getLocation(LocationType type) {
        double x, z, y;
        float yaw, pitch;
        String world;
        world = gameFile.getString("Locations." + LocationType.toString(type) + ".world");
        x = gameFile.getDouble("Locations." + LocationType.toString(type) + ".x");
        y = gameFile.getDouble("Locations." + LocationType.toString(type) + ".y");
        z =	gameFile.getDouble("Locations." + LocationType.toString(type) + ".z");
        yaw = (float) gameFile.getDouble("Locations." + LocationType.toString(type) + ".yaw");
        pitch = (float) gameFile.getDouble("Locations." + LocationType.toString(type) + ".pitch");
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
