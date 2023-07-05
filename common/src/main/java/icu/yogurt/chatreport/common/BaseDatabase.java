package icu.yogurt.chatreport.common;

import icu.yogurt.chatreport.common.connector.DatabaseConnector;
import icu.yogurt.chatreport.common.interfaces.IDatabase;
import icu.yogurt.chatreport.common.model.User;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDatabase implements IDatabase {

    protected final BasePlugin plugin;
    protected final DatabaseConnector connector;

    public BaseDatabase(BasePlugin plugin, DatabaseConnector connector){
        this.plugin = plugin;
        this.connector = connector;
        createTableIfNotExist();
    }

    @Nullable
    @Override
    public User getUser(String param, Connection connection, String query) throws SQLException{
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, param);

        ResultSet result = statement.executeQuery();

        if (result.next()) {
            return new User(result.getString("username"),
                    result.getString("uuid"),
                    null);
        } else {
            return null;
        }
    }
}
