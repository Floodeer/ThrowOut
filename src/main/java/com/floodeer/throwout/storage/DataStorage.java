package com.floodeer.throwout.storage;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.game.GamePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DataStorage {

    private final Database db;

    public DataStorage(Database db) {
        this.db = db;
    }

    public void loadPlayer(String uuid, GamePlayer gp) {
        if(!db.checkConnection()) {
            ThrowOut.get().getLogger().severe("Database connection is not defined.");
            return;
        }

        new OperationWriter() {
            final Connection conn = db.getConnection();
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            @Override
            public void onWrite() throws SQLException {
                if (!checkPlayer(uuid)) {
                    insertPlayer(uuid);
                }

                String query = "SELECT games_played, wins, losses, kills, deaths, received_hits, hits, balance " +
                        "FROM throwout_player " +
                        "WHERE uuid = ? LIMIT 1;";

               try {
                   preparedStatement = conn.prepareStatement(query);
                   preparedStatement.setString(1, uuid);
                   resultSet = preparedStatement.executeQuery();
                   if(resultSet != null && resultSet.next()) {
                       gp.setGamesPlayed(resultSet.getInt("games_played"));
                       gp.setWins(resultSet.getInt("wins"));
                       gp.setLosses(resultSet.getInt("losses"));
                       gp.setKills(resultSet.getInt("kills"));
                       gp.setDeaths(resultSet.getInt("deaths"));
                       gp.setTotalHitsReceived(resultSet.getInt("received_hits"));
                       gp.setTotalHits(resultSet.getInt("hits"));
                       gp.setBalance(resultSet.getInt("balance"));
                   }
               }finally {
                   if(resultSet != null)
                       resultSet.close();
                   if(preparedStatement != null)
                       preparedStatement.close();
               }

            }
        }.writeOperation(db.getExecutor(), ThrowOut.get().getLogger(),"An internal error occurred while loading " + gp.getName() + "'s data.");
    }

    public void savePlayer(GamePlayer gp) {
        if(!db.checkConnection()) {
            ThrowOut.get().getLogger().severe("Database connection is not defined.");
            return;
        }

        Connection conn = db.getConnection();
        PreparedStatement preparedStatement = null;
        StringBuilder query = new StringBuilder();
        query.append("UPDATE throwout_player SET ");
        query.append("playername = ?, games_played = ?, wins = ?, losses = ?, kills = ?, deaths = ? ");
        query.append("received_hits = ?, hits = ?, balance = ?");
        query.append("WHERE uuid = ?");

        try {
            preparedStatement = conn.prepareStatement(query.toString());
            preparedStatement.setString(1, gp.getName());
            preparedStatement.setInt(2, gp.getGamesPlayed());
            preparedStatement.setInt(3, gp.getWins());
            preparedStatement.setInt(4, gp.getLosses());
            preparedStatement.setInt(5, gp.getKills());
            preparedStatement.setInt(6, gp.getDeaths());
            preparedStatement.setInt(7, gp.getTotalHitsReceived());
            preparedStatement.setInt(9, gp.getBalance());
            preparedStatement.setString(10, gp.getUuid().toString());
            preparedStatement.execute();

        }catch(SQLException ex) {
            ThrowOut.get().getLogger().severe("An internal error occurred while saving " + gp.getName() + "'s data.");
            ex.printStackTrace();
        }finally {
            if(preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch(SQLException ignored) {
                }
        }
    }

    public void savePlayerAsync(GamePlayer gp) {
        if(!db.checkConnection()) {
            ThrowOut.get().getLogger().severe("Database connection is not defined.");
            return;
        }

        new OperationWriter() {
            final Connection conn = db.getConnection();
            PreparedStatement preparedStatement = null;
            @Override
            public void onWrite() throws SQLException {
                StringBuilder query = new StringBuilder();
                query.append("UPDATE throwout_player SET ");
                query.append("playername = ?, games_played = ?, wins = ?, losses = ?, kills = ?, deaths = ? ");
                query.append("received_hits = ?, hits = ?, balance = ?");
                query.append("WHERE uuid = ?");
                try {
                    preparedStatement = conn.prepareStatement(query.toString());
                    preparedStatement.setString(1, gp.getName());
                    preparedStatement.setInt(2, gp.getGamesPlayed());
                    preparedStatement.setInt(3, gp.getWins());
                    preparedStatement.setInt(4, gp.getLosses());
                    preparedStatement.setInt(5, gp.getKills());
                    preparedStatement.setInt(6, gp.getDeaths());
                    preparedStatement.setInt(7, gp.getTotalHitsReceived());
                    preparedStatement.setInt(8, gp.getTotalHits());
                    preparedStatement.setInt(9, gp.getBalance());
                    preparedStatement.setString(10, gp.getUuid().toString());
                    preparedStatement.execute();
                }finally {
                    if(preparedStatement != null)
                        preparedStatement.close();
                }
            }
        }.writeOperation(db.getExecutor(), ThrowOut.get().getLogger(),"An internal error occurred while saving " + gp.getName() + "'s data.");
    }

    public void insertPlayer(String uid) {
        if (!db.checkConnection()) {
            return;
        }

        UUID uuid = UUID.fromString(uid);
        PreparedStatement preparedStatement = null;

        try {

            String queryBuilder = "INSERT INTO `throwout_player` " +
                    "(`player_id`, `uuid`, `playername`) " +
                    "VALUES " +
                    "(NULL, ?, ?);";
            preparedStatement = db.getConnection().prepareStatement(queryBuilder);
            preparedStatement.setString(1, uid);
            preparedStatement.setString(2, ThrowOut.get().getServer().getPlayer(uuid).getName());
            preparedStatement.executeUpdate();

        } catch (final SQLException sqlException) {
            sqlException.printStackTrace();

        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (final SQLException ignored) {
                }
            }
        }
    }

    public boolean checkPlayer(String uuid) {
        if (!db.checkConnection()) {
            return false;
        }

        int count = 0;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            String queryBuilder = "SELECT Count(`player_id`) " +
                    "FROM `throwout_player` " +
                    "WHERE `uuid` = ? " +
                    "LIMIT 1;";

            preparedStatement = db.getConnection().prepareStatement(queryBuilder);
            preparedStatement.setString(1, uuid);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (final SQLException sqlException) {
            sqlException.printStackTrace();

        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (final SQLException ignored) {
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (final SQLException ignored) {
                }
            }
        }

        return count > 0;
    }
}
