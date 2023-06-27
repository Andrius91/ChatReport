package icu.yogurt.chatreport.common.connector;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.model.UserModel;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class DatabaseConnector {
    private final String url;
    private final String user;
    private final String password;

    private final BasePlugin plugin;

    public DatabaseConnector(String url, String user, String password, BasePlugin plugin) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.plugin = plugin;
        createTableIfNotExist();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void createTableIfNotExist() {
        try (Connection connection = getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS users(" +
                    "username VARCHAR(16)," +
                    "uuid VARCHAR(36)," +
                    "creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (uuid)" +
                    ")";
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            plugin.log(1, "Error during creating table: " + e.getMessage());
        }
    }
    @SuppressWarnings("unused")
    public boolean verifyUserByUUID(String uuid) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);

            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            plugin.log(1, "Error during verifying user: " + e.getMessage());
            return false;
        }
    }
    @SuppressWarnings("unused")
    public boolean verifyUserByUsername(String name) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            return result.next();
        } catch (SQLException e) {
            plugin.log(1, "Error during verifying user: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public UserModel getUserByUUID(String uuid) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE uuid = ? ORDER BY creationDate DESC LIMIT 1";
            return getUserModel(uuid, connection, query);
        } catch (SQLException e) {
            plugin.log(1, "Error during getting user: " + e.getMessage());
            return null;
        }
    }

    public UserModel getUserByUsername(String username) {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? ORDER BY creationDate DESC LIMIT 1";
            return getUserModel(username, connection, query);
        } catch (SQLException e) {
            plugin.log(1, "Error during getting user: " + e.getMessage());
            return null;
        }
    }

    @Nullable
    private UserModel getUserModel(String param, Connection connection, String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, param);

        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return new UserModel(result.getString("username"),
                    result.getString("uuid"),
                    null);
        } else {
            return null;
        }
    }

    public void createUser(UserModel user) {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO users (username, uuid) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getUuid());

            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException ignored){

        } catch (SQLException e) {
            plugin.log(1,"Error during creating user: " + e.getMessage());
        }
    }
}
