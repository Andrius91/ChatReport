package icu.yogurt.chatreport.common;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import icu.yogurt.chatreport.common.interfaces.IChatReport;
import icu.yogurt.chatreport.common.interfaces.IDatabase;
import icu.yogurt.chatreport.common.interfaces.IStorage;
import icu.yogurt.chatreport.common.managers.ConfigManager;
import icu.yogurt.chatreport.common.managers.DatabaseManager;
import icu.yogurt.chatreport.common.managers.StorageManager;
import icu.yogurt.chatreport.common.managers.TaskManager;
import icu.yogurt.chatreport.common.service.ChatReportService;
import icu.yogurt.chatreport.common.service.PunishmentService;
import icu.yogurt.chatreport.common.service.ReportService;
import lombok.Getter;
import lombok.Setter;
import org.simpleyaml.configuration.file.YamlFile;

@Getter
public abstract class BasePlugin implements IChatReport {

    private static BasePlugin plugin;

    private TaskManager taskManager;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private StorageManager storageManager;

    private IStorage storage;
    private IDatabase database;

    private API api;

    private PunishmentService punishmentService;
    private ChatReportService chatReportService;
    private ReportService reportService;

    @Setter
    private boolean isDebug;

    public abstract boolean isRedisBungeeAvailable();
    protected abstract void registerCommands();
    protected abstract void registerListeners();

    public void onLoad(){
        plugin = this;

        runAsync(() -> {
            this.configManager = new ConfigManager(this);

            this.databaseManager = new DatabaseManager(this);
            this.storageManager = new StorageManager(this);

            this.database = databaseManager.initDatabase();
            this.storage = storageManager.initStorage();

            this.api = new API();

            this.reportService = new ReportService(this);
            this.punishmentService = new PunishmentService(this);
            this.chatReportService = new ChatReportService(this);

            this.taskManager = new TaskManager(this, api);
        });
    }
    protected void onEnable(){

        // Load tasks
        taskManager.loadTasks();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Register channels
        registerChannels();
    }
    protected void onDisable(){
        if(storageManager.getConnector() != null){
            storageManager.close(); // Close the Redis connection
        }
        if(database != null){
            databaseManager.closeDataSource(); // Close the database connection
        }

        if(getAudienceProvider() != null){
            getAudienceProvider().close();
        }
        unregisterChannels();
    }

    public RedisBungeeAPI getRedisBungeeAPI(){
        return RedisBungeeAPI.getRedisBungeeApi();
    }

    private void registerChannels(){
        if(isRedisBungeeAvailable()){
            RedisBungeeAPI.getRedisBungeeApi().registerPubSubChannels("pandora:report");
        }
    }

    private void unregisterChannels(){
        if(isRedisBungeeAvailable()){
            RedisBungeeAPI.getRedisBungeeApi().unregisterPubSubChannels("pandora:report");
        }
    }

    public YamlFile getConfig(){
        return configManager.getConfig();
    }

    public YamlFile getPunishmentConfig(){
        return configManager.getPunishmentConfig();
    }

    public YamlFile getLangConfig(){
        return configManager.getLangConfig();
    }
    public static BasePlugin getPlugin(){
        return plugin;
    }
}
