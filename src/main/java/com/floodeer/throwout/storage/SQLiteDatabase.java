package com.floodeer.throwout.storage;

import com.floodeer.throwout.ThrowOut;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteDatabase implements Database {

    private static final Logger LOGGER = ThrowOut.get().getLogger();

    private final String connectionUri;

    private final AtomicReference<Connection> connection;
    private final ExecutorService pool;

    public SQLiteDatabase(String database) throws ClassNotFoundException, SQLException {
        String driver= ThrowOut.get().getConfigOptions().databaseDriver;
        this.connectionUri = String.format("jdbc:%s:%s", getDriver(driver), database);
        this.connection = new AtomicReference<>();
        this.pool = Executors.newCachedThreadPool();
        Class.forName(driver);
        connect();
    }

    @Override
    public void connect() throws SQLException {
        Connection conn = connection.get();
        if (conn != null && !conn.isClosed()) {
            try {
                conn.createStatement().execute("SELECT 1;");
            } catch (SQLException sqlException) {
                if (sqlException.getSQLState().equals("08S01")) {
                    connection.set(null);
                }
            }
        }
        if (connection.get() == null || connection.get().isClosed()) {
            connection.set(DriverManager.getConnection(connectionUri));
        }
    }

    @Override
    public void close() {
        Connection conn = connection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing connection", e);
            } finally {
                connection.set(null);
            }
        }
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
        }
    }

    @Override
    public Connection getConnection() {
        return connection.get();
    }

    @Override
    public Executor getExecutor() {
        return pool;
    }

    @Override
    public boolean checkConnection() {
        try {
            connect();
        } catch (SQLException ex) {
            close();
            LOGGER.log(Level.SEVERE, "Connection check failed", ex);
            return false;
        }
        return true;
    }

    @Override
    public void createTables() throws IOException, SQLException {
        URL resource = getClass().getResource("/tables.sql");
        if (resource == null) {
            throw new IOException("Resource /tables.sql not found.");
        }
        String script = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(resource.getPath())));

        try (Statement statement = connection.get().createStatement()) {
            connection.get().setAutoCommit(false);
            for (String query : script.split(";")) {
                query = query.trim();
                if (!query.isEmpty()) {
                    statement.execute(query);
                }
            }
            connection.get().commit();
        } catch (SQLException e) {
            connection.get().rollback();
            throw e;
        } finally {
            connection.get().setAutoCommit(true);
        }
    }

    @Override
    public boolean columnExists(String column) {
        try {
            DatabaseMetaData metadata = connection.get().getMetaData();
            try (ResultSet resultSet = metadata.getTables(null, null, "throwout_player", null)) {
                if (resultSet.next()) {
                    try (ResultSet columns = metadata.getColumns(null, null, "throwout_player", column)) {
                        return columns.next();
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking column existence", e);
        }
        return false;
    }

    @Override
    public void addColumn(String value) throws SQLException {
        String query = String.format("ALTER TABLE throwout_player ADD COLUMN %s", value);

        try (Statement statement = connection.get().createStatement()) {
            connection.get().setAutoCommit(false);
            statement.execute(query);
            connection.get().commit();
        } catch (SQLException e) {
            connection.get().rollback();
            throw e;
        } finally {
            connection.get().setAutoCommit(true);
        }
    }

    private String getDriver(String driver) {
        if (driver.equals("org.sqlite.JDBC")) {
            return "sqlite";
        } else if (driver.equals("org.h2.Driver")) {
            return "h2";
        } else {
            throw new IllegalArgumentException("Unsupported driver: " + driver);
        }
    }
}