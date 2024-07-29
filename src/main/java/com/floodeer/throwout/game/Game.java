package com.floodeer.throwout.game;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.util.Util;
import com.floodeer.throwout.util.update.UpdateEvent;
import com.floodeer.throwout.util.update.UpdateType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Getter
public class Game implements Listener {

    private final String arena;

    private List<GamePlayer> players;
    private Map<GamePlayer, Integer> kills, deaths;

    private int timeLeft;
    @Getter @Setter
    private boolean canStart;

    @Setter
    private GameState state;

    private GameArena gameArena;

    private int preStart;

    public Game(String arena, boolean load) {
        this.arena = arena;
        gameArena = new GameArena(arena);
        setCanStart(false);
        setState(GameState.PRE_GAME);

        if(load) {
            loadGame();
        }
    }

    public void loadGame() {
        players = Lists.newArrayList();
        kills = Maps.newHashMap();
        preStart = ThrowOut.get().getConfigOptions().preStartCountdown;

        ThrowOut.get().getServer().getPluginManager().registerEvents(this, ThrowOut.get());
    }

    @EventHandler
    public void onGameUpdate(UpdateEvent event) {
        if(event.getType() != UpdateType.SEC)
            return;

        if(getState() == GameState.IN_GAME) {
            if(checkForWinner())
                return;

            updateGameScoreboard();

        }else if(getState() == GameState.PRE_GAME || getState() == GameState.STARTING) {

            updateLobbyScoreboard();
        }
    }

    public void addPlayer(GamePlayer gp) {
        Player player = gp.getPlayer();
        player.setAllowFlight(false);

        getPlayers().add(gp);
        gp.clearInventory(true);
        gp.setInGame(true);
        gp.setGame(this);
        kills.put(gp, 0);
        deaths.put(gp, 0);
        player.teleport(getGameArena().getLocation(GameArena.LocationType.LOBBY));

        ThrowOut.get().getServer().getScheduler().runTaskLater(ThrowOut.get(), () -> {
            player.setGameMode(GameMode.ADVENTURE);
            getGameArena().updateSign();
        }, 10);
    }

    public void removePlayer(GamePlayer gp, boolean force) {
        if(getPlayers().contains(gp)) {
            if(getState() == GameState.IN_GAME) {
                gp.setGamesPlayed(gp.getGamesPlayed() + 1);
                gp.setLosses(gp.getLosses() + 1);
            }

            if (!force) {
                getPlayers().remove(gp);
                if (getState() == GameState.STARTING) {
                    if (this.getPlayers().size() < getGameArena().getMinPlayers()) {
                        canStart = true;
                        setState(GameState.PRE_GAME);
                        getGameArena().updateSign();
                    }
                }
            }
        }

        GameScoreboard.removeScore(gp.getPlayer());
        GameScoreboard.getScoreboards().keySet().stream().filter(cur -> GamePlayer.get(cur).getGame().equals(this)).forEach(cur -> {
            GameScoreboard scoreboard = GameScoreboard.getByUUID(cur);
            getPlayers().forEach(players -> scoreboard.removeFromTeam(gp.getPlayer()));
        });

        gp.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        gp.setInGame(false);
        gp.setGame(null);
        gp.setSpectator(false);
        gp.clearInventory(false);
        gp.restoreInventory();
    }

    public boolean checkForWinner() {
        Optional<Map.Entry<GamePlayer, Integer>> killsAmount = kills.entrySet().stream()
                .filter(entry -> entry.getValue() >= ThrowOut.get().getConfigOptions().maxKills)
                .findFirst();

        if(killsAmount.isPresent()) {
            endGame(false, killsAmount.get().getKey());
            return true;
        }

        if(timeLeft == 0) {
            Map.Entry<GamePlayer, Integer> killsEntry = kills.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);

            GamePlayer winner = (killsEntry != null) ? killsEntry.getKey() : null;
            endGame(false, winner);
            return true;
        }

        return false;
    }


    public void endGame(boolean shutdown, @Nullable GamePlayer winner) {
        setState(GameState.ENDING);
        requestBungeeCordUpdate();

        if(winner == null) {
            //TODO
            shutdown(!shutdown);
            return;
        }

        winner.setWins(winner.getWins() + 1);


        ThrowOut.get().getServer().getScheduler().runTaskLater(ThrowOut.get(), () -> shutdown(true), ThrowOut.get().getConfigOptions().endingDuration * 20L);
    }

    public void shutdown(boolean recreate) {
        if (!getPlayers().isEmpty()) {
            getPlayers().forEach(gp -> {
                removePlayer(gp, true);
                if (getState() == GameState.IN_GAME) {
                    broadcast(ThrowOut.get().getLanguage().gameCanceled);
                }
            });
        }
        getPlayers().clear();
        kills.clear();
        deaths.clear();
        requestBungeeCordUpdate();
        HandlerList.unregisterAll(this);

        if(recreate) {
            ThrowOut.get().getGameManager().recreateGame(this);
        }
    }

    private void updateGameScoreboard() {
        Date date = new Date(timeLeft * 1000L);
        String timeLeft = new SimpleDateFormat("mm:ss").format(date);

        getPlayers().forEach(gp -> {
            GameScoreboard scoreboard;
            if(GameScoreboard.hasScore(gp.getPlayer()))
                scoreboard = GameScoreboard.getByPlayer(gp.getPlayer());
            else {
                scoreboard = GameScoreboard.createScore(gp.getPlayer());
                scoreboard.setTitle(ThrowOut.get().getLanguage().gameScoreboardName);
            }

            List<String> lines = Lists.newArrayList();
            for(String line : ThrowOut.get().getLanguage().gameScoreboard) {
                line = Util.color(line);
                line = line.replace("%time_left%", timeLeft);
                line = line.replace("%kills%", Integer.toString(getKills().get(gp)));
                for(int i = 1; i <= 10; i++) {
                    line = line.replace("%pos" + i + "%", getPosition(i));
                }
                if (line.equalsIgnoreCase(" ")) {
                    line = Util.createSpacer();
                }

                lines.add(line);
            }

            scoreboard.setSlotsFromList(lines);
        });
    }

    public void updateLobbyScoreboard() {
        getPlayers().forEach(gp -> {
            GameScoreboard scoreboard;
            if (!GameScoreboard.hasScore(gp.getPlayer())) {
                scoreboard = GameScoreboard.createScore(gp.getPlayer());
                scoreboard.setTitle(ThrowOut.get().getLanguage().lobbyScoreboardName);
            } else {
                scoreboard = GameScoreboard.getByPlayer(gp.getPlayer());
            }

            List<String> lines = Lists.newArrayList();
            for (String s : ThrowOut.get().getLanguage().lobbyBoard) {
                s = Util.color(s);
                s = s.replace("%players%", String.valueOf(getPlayers().size()));
                s = s.replace("%mapname%", getArena());
                s = s.replace("%minplayers%", String.valueOf(getGameArena().getMinPlayers()));
                s = s.replace("%maxplayers%", String.valueOf(getGameArena().getMaxPlayers()));
                s = s.replace("%timer%", String.valueOf(preStart));
                s = s.replace("%capitalize_state%", Util.color(WordUtils.capitalize(this.getState().toString())));
                s = s.replace("%state%", getState().toString());
                s = s.replace("%wins%", Integer.toString(gp.getWins()));
                s = s.replace("%games_played%", Integer.toString(gp.getGamesPlayed()));
                s = s.replace("%balance%", Integer.toString(gp.getBalance()));

                if (s.equalsIgnoreCase(" ")) {
                    s = Util.createSpacer();
                }
                lines.add(s);
            }
            scoreboard.setSlotsFromList(lines);
            lines.clear();
        });
    }

    private String getPosition(int pos) {
        List<Map.Entry<GamePlayer, Integer>> places = kills.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(reverseOrder()))
                .limit(10)
                .collect(toList());

        if (places.size() <= pos)
            return "";
        if (places.get(pos) == null)
            return "";

        return places.get(pos).getKey().getName();
    }

    private void requestBungeeCordUpdate() {

    }

    public void broadcast(String msg) {
        getPlayers().forEach(cur -> cur.getPlayer().sendMessage(Util.color(msg)));
    }

    public enum GameState {
        PRE_GAME,
        STARTING,
        IN_GAME,
        ENDING;
    }
}
