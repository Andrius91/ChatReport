package icu.yogurt.chatreport.common.managers;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.connector.DatabaseConnector;
import icu.yogurt.chatreport.common.database.MySQL;
import icu.yogurt.chatreport.common.database.SQLite;
import icu.yogurt.chatreport.common.interfaces.IDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final BasePlugin plugin;
    private final DatabaseConnector connector;

    public DatabaseManager(BasePlugin plugin){
        this.plugin = plugin;
        this.connector = new DatabaseConnector(plugin);
    }

    public IDatabase initDatabase(){
        String database_type = plugin.getConfig().getString("database.type");

        if(database_type.equalsIgnoreCase("SQLite")){
            return new SQLite(plugin, connector);
        }else if(database_type.equalsIgnoreCase("MySQL")){
            return new MySQL(plugin, connector);
        }else{
            throw new RuntimeException("No support found for the selected database type.");
        }
    }

    public Connection getConnection(){
        try{
            return connector.getConnection();
        } catch (SQLException e){
            throw new RuntimeException("Error getting connection: " + e);
        }
    }
    public void closeDataSource(){
        connector.closeDataSource();
    }
}
