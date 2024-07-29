package com.floodeer.throwout.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConfigOptions extends Configuration {

    public ConfigOptions(File configFile) {
        super(configFile);
    }

    @ConfigOptions(name = "Game.Max-Kills")
    public int maxKills = 30;

    @ConfigOptions(name = "Game.Game-Length")
    public int gameLength = 300;

    @ConfigOptions(name = "Game.Pre-Start-Countdown")
    public int preStartCountdown = 15;

    @ConfigOptions(name = "Game.Ending-Duration")
    public int endingDuration = 15;
}
