package com.floodeer.throwout.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MessagesConfig extends Configuration {

    public MessagesConfig(File configFile) {
        super(configFile);
    }

    @ConfigOptions(name = "Game.Game-Canceled")
    public String gameCanceled = "&cGame canceled by an Administrator.";

    @ConfigOptions(name = "Scoreboard.Game-Name")
    public String gameScoreboardName = "&e&lTHROW OUT";

    @ConfigOptions(name = "Scoreboard.Game-Format")
    public List<String> gameScoreboard = Arrays.asList(" ",
            "&7%date%",
            "",
            "Time Left: &a%time_left%",
            "Your Kills: &a%kills%",
            " ",
            "Top Players:",
            "%pos_1%",
            "%pos_2%",
            "%pos_3%",
            "%pos_4%",
            "%pos_5%",
            " ",
            "yourserver.com");

    @ConfigOptions(name = "Scoreboard.Lobby-Name")
    public String lobbyScoreboardName = "&e&lTHROW OUT";

    @ConfigOptions(name = "Scoreboard.Lobby-Format")
    public List<String> lobbyBoard = Arrays.asList(" ",
            "Map: &a%mapname%",
            "Players: &b%players%/%maxplayers%",
            "Needed: &b%minplayers%",
            " ",
            "State: %capitalize_state%",
            " ",
            "Balance: &a%balance%",
            " ",
            "yourserver.com");

}
