package icu.yogurt.chatreport.common.connector;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import icu.yogurt.chatreport.common.BasePlugin;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static icu.yogurt.chatreport.common.ConfigKeys.DATABASE_PASSWORD;
import static icu.yogurt.chatreport.common.ConfigKeys.DATABASE_USER;

public class DatabaseConnector {
    private final HikariDataSource ds;
    private final BasePlugin plugin;
    public DatabaseConnector(BasePlugin plugin) {
        this.plugin = plugin;
        boolean isSQLite = plugin.getConfig()
                .getString("database.type")
                .equalsIgnoreCase("SQLITE");

        HikariConfig config = new HikariConfig();

        if (isSQLite) {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl(buildSQLiteJdbcUrl());
        } else {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl(buildMySqlJdbcUrl());
            config.setUsername(DATABASE_USER.get());
            config.setPassword(DATABASE_PASSWORD.get());
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        this.ds = new HikariDataSource(config);
    }

    @SneakyThrows
    private String buildSQLiteJdbcUrl() {
        File databaseFile = createDatabaseFile();
        return "jdbc:sqlite:" + databaseFile.getAbsolutePath();
    }

    private String buildMySqlJdbcUrl() {
        return plugin.getConfig().getString("database.url");
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public void closeDataSource() {
        if (ds != null) {
            ds.close();
        }
    }

    private File createDatabaseFile() {
        File databaseFile = new File(plugin.getDataFolder(), "database.db");
        try {
            if (!databaseFile.exists()) {
                boolean created = databaseFile.createNewFile();
                if (!created) {
                    throw new RuntimeException("Failed to create SQLite database file: " + databaseFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error creating SQLite database file: " + databaseFile.getAbsolutePath(), e);
        }
        return databaseFile;
    }
}

