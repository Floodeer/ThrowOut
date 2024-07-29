package com.floodeer.throwout.manager;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.game.Game;
import com.floodeer.throwout.game.GamePlayer;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class GameManager {

    @Getter
    private List<Game> games = new ArrayList<>();
    private Queue<GamePlayer> queue = Lists.newLinkedList();

    public Game createGame(String gameName, boolean load) {
        final Game game = new Game(gameName, load);
        games.add(game);
        if(load && game.getGameArena().doesMapExists() && game.getGameArena().isSignSet()) {
            game.getGameArena().updateSign();
            ThrowOut.get().getServer().getScheduler().runTaskLater(ThrowOut.get(), () -> {
                if(!queue.isEmpty()) {
                    while (canJoin(game) && !queue.isEmpty()) {
                        game.addPlayer(queue.poll());
                    }
                }
            }, 20);
        }
        return game;
    }

    public void deleteGame(String name) throws IOException {
        Game game = getGameFromName(name);
        if(game.getState() == Game.GameState.IN_GAME)
            game.shutdown(false);
        game.getGameArena().deleteArena();
        games.remove(game);
    }

    public void reload(String name) {
        Game game = getGameFromName(name);
        game.shutdown(false);
        getGames().remove(game);
        createGame(game.getArena(), true);
    }

    public boolean doesGameExists(String map) {
        File mapsFolder = new File(ThrowOut.get().getDataFolder(), "maps");
        if (!mapsFolder.exists() || !mapsFolder.isDirectory()) {
            return false;
        }

        File[] files = mapsFolder.listFiles();
        if (files == null) {
            return false;
        }

        String targetMapName = map.toLowerCase();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.toLowerCase().endsWith(".yml") &&
                    fileName.substring(0, fileName.length() - 4).equalsIgnoreCase(targetMapName)) {
                return true;
            }
        }
        return false;
    }

    public Game getGameFromName(String name) {
        for (Game game : games) {
            if (game.getArena().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }

    public Game recreateGame(Game game) {
        this.games.remove(game);
        Game g = createGame(game.getArena(), true);
        g.getGameArena().updateSign();
        return g;
    }

    public void shutdownGames() {
        for(Game g : games) {
            g.setState(Game.GameState.ENDING);
            g.shutdown(false);
        }
    }

    public Game findGameFor(GamePlayer gp) {
        Game result = null;
        List<Game> games = getGames();
        Collections.shuffle(games);
        for (Game g : games) {
            if (!g.getPlayers().isEmpty()) {
                if (canJoin(g)) {
                    result = g;
                    break;
                }
            }
            if (canJoin(g))
                result = g;
        }
        if (result == null) {
            addToQueue(gp);
            return null;
        }

        return result;
    }

    public boolean canJoin(Game g) {
        return preGameState(g) &&
                g.getPlayers().size() < g.getGameArena().getMaxPlayers();
    }

    public boolean preGameState(Game game) {
        return game.getState() == Game.GameState.PRE_GAME || game.getState() == Game.GameState.STARTING;
    }

    public Game getNextGame(GamePlayer optionalPlayer) {
        for (Game game : games) {
            if (game.getState() == Game.GameState.PRE_GAME) {
                if (game.getPlayers().size() < game.getGameArena().getMaxPlayers()) {
                    return game;
                } else if (optionalPlayer != null && game.getPlayers().size() >= game.getGameArena().getMaxPlayers() && optionalPlayer.getPlayer().hasPermission("throwout.joinfull"))
                    return game;
            }
        }
        return null;
    }

    public List<Game> populate(boolean onlyIfCanJoin) {
        return onlyIfCanJoin ?
                getGames().stream().filter(this::canJoin).collect(Collectors.toList()) :
                getGames();
    }

    public void addToQueue(GamePlayer gp) {
        queue.add(gp);
    }
}