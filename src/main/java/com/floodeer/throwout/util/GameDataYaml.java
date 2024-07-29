package com.floodeer.throwout.util;

import com.floodeer.throwout.ThrowOut;

import java.io.File;


public class GameDataYaml {

    public static GameDataFile getMap(String mapName) {
        return new GameDataFile(ThrowOut.get().getDataFolder().getAbsolutePath() +
                File.separator + "maps" + File.separator + mapName + File.separator + mapName + ".yml");
    }
}