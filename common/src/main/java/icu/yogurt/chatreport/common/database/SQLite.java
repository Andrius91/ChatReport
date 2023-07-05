package icu.yogurt.chatreport.common.database;

import icu.yogurt.chatreport.common.BaseDatabase;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.connector.DatabaseConnector;
import icu.yogurt.chatreport.common.model.User;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends BaseDatabase {

    public SQLite(BasePlugin plugin, DatabaseConnector connector){
        super(plugin, connector);
    }

    public void createTableIfNotExist() {
        try (Connection connection = connector.getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS users(" +
                    "username TEXT(16)," +
                    "uuid TEXT(36) PRIMARY KEY," +
                    "creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            plugin.log(1, "Error during creating table: " + e.getMessage());
        }
    }

    @Override
    public User getUserByUsername(@NotNull String username) {
        try (Connection connection = connector.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? ORDER BY creationDate DESC LIMIT 1";
            return getUser(username, connection, query);
        } catch (SQLException e) {
            plugin.log(1, "Error during getting user: " + e.getMessage());
            return null;
        }
    }


    @Override
    public void createUser(User user) {
        try (Connection connection = connector.getConnection()) {
            String query = "INSERT INTO users (username, uuid) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getUuid());

            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.log(1,"Error during creating user: " + e.getMessage());
        }
    }
}
