package com.floodeer.throwout.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ConfigOptions extends Configuration {

    public ConfigOptions(File configFile) {
        super(configFile, Arrays.asList(
                "#####################################################################",
                " ",
                "Important Config Info",
                " ",
                "#### Database Drivers ####",
                " ",
                "SQLite: org.sqlite.JDBC",
                "H2 (recommended over SQLite): org.h2.Driver",
                "MySQL: com.mysql.jdbc.Driver",
                "MariaDB (recommended over MySQL): org.mariadb.jdbc.Driver"
                ));
    }

    @ConfigOptions(name = "Database.Driver")
    public String databaseDriver = "org.sqlite.JDBC";

    @ConfigOptions(name = "Database.Host")
    public String dbHost = "localhost";

    @ConfigOptions(name = "Database.Port")
    public int dbPort = 3306;

    @ConfigOptions(name = "Database.User")
    public String dbUser = "user";

    @ConfigOptions(name = "Database.Password")
    public String dbPassword = "123456";

    @ConfigOptions(name = "Database.Database")
    public String dbDatabase = "database";

    @ConfigOptions(name = "Game.Max-Kills")
    public int maxKills = 30;

    @ConfigOptions(name = "Game.Game-Length")
    public int gameLength = 300;

    @ConfigOptions(name = "Game.Pre-Start-Countdown")
    public int preStartCountdown = 15;

    @ConfigOptions(name = "Game.Ending-Duration")
    public int endingDuration = 15;

    @ConfigOptions(name = "Game.Damage.Percentage-Per-Hit-Min")
    public int hitPercentageMin = 2;

    @ConfigOptions(name = "Game.Damage.Percentage-Per-Hit-Max")
    public int hitPercentageMax = 3;

    @ConfigOptions(name = "Game.Damage.Percentage-Mega-Punch-Min")
    public int megaPunchPercentageMin = 10;

    @ConfigOptions(name = "Game.Damage.Percentage-Mega-Punch-Max")
    public int megaPunchPercentageMax = 25;
}
