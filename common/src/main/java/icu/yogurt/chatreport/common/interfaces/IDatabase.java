package icu.yogurt.chatreport.common.interfaces;

import icu.yogurt.chatreport.common.model.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabase {
    void createTableIfNotExist();
    User getUserByUsername(String username);

    User getUser(String param, Connection connection, String query) throws SQLException;
    void createUser(User user);
}
