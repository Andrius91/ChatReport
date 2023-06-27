package icu.yogurt.chatreport.common;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import icu.yogurt.chatreport.common.cache.UserCache;
import icu.yogurt.chatreport.common.config.Config;
import icu.yogurt.chatreport.common.connector.DatabaseConnector;
import icu.yogurt.chatreport.common.connector.RedisConnector;
import icu.yogurt.chatreport.common.interfaces.IChatReport;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.interfaces.IStorage;
import icu.yogurt.chatreport.common.model.Message;
import icu.yogurt.chatreport.common.model.Report;
import icu.yogurt.chatreport.common.model.ReportOption;
import icu.yogurt.chatreport.common.service.PunishmentService;
import icu.yogurt.chatreport.common.storage.RedisStorage;
import icu.yogurt.chatreport.common.storage.YamlStorage;
import icu.yogurt.chatreport.common.task.ActivePlayerPunishmentTask;
import icu.yogurt.chatreport.common.task.PunishmentProcessingTask;
import lombok.Getter;
import lombok.Setter;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.serialization.ConfigurationSerialization;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public abstract class BasePlugin implements IChatReport {

    private RedisConnector redisConnector;
    private IStorage storage;
    private DatabaseConnector database;
    private YamlFile config;
    private YamlFile punishmentConfig;
    private YamlFile langConfig;
    private API api;
    private UserCache userCache;
    private PunishmentService punishmentService;

    @Setter
    private boolean isDebug;

    protected abstract void registerCommands();
    protected abstract void registerListeners();

    public void onLoad(){
        // Load configs
        loadConfigs();
        runAsync(() -> {
            this.storage = initStorage();
            this.database = initDatabaseConnector();
            this.punishmentService = new PunishmentService(this);
            this.api = initAPI();
        });

    }
    protected void onEnable(){

        // Load config serializers
        loadConfigsSerializers();

        // Load tasks
        loadTasks();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        // Register channels
        registerChannels();
    }
    protected void onDisable(){
        if(redisConnector != null){
            redisConnector.close(); // Close the Redis connection
        }
        if(getAudienceProvider() != null){
            getAudienceProvider().close();
        }
        unregisterChannels();
    }

    protected void loadTasks(){
        boolean auto_task = punishmentConfig.getBoolean("punishment.auto-task");
        boolean auto_on_join = punishmentConfig.getBoolean("punishment.auto-on-join");

        if(auto_task){
            scheduledTask(new PunishmentProcessingTask(this), 5L, 5L, TimeUnit.SECONDS);
        }
        if(auto_on_join){
            scheduledTask(new ActivePlayerPunishmentTask(this), 5L, 5L, TimeUnit.SECONDS);
        }
    }
    @Override
    public RedisBungeeAPI getRedisBungeeAPI(){
        return RedisBungeeAPI.getRedisBungeeApi();
    }

    private void loadConfigs(){
        this.config = new Config().get(this, "config.yml");
        this.langConfig = new Config().get(this, "lang.yml");
        this.punishmentConfig = new Config().get(this, "punishment.yml");
    }

    private IStorage initStorage(){
        // Storage
        String storage_type = config.getString("storage.type");
        if(storage_type.equalsIgnoreCase("REDIS")){
            String url = config.getString("storage.url");
            redisConnector = new RedisConnector(url);
            this.userCache = new UserCache(this, redisConnector);
            return new RedisStorage(this, redisConnector);
        } else {
            return new YamlStorage();
        }
    }

    private API initAPI(){
        String api_host = config.getString("api.host");
        String api_key = config.getString("api.key");

        if(!api_host.isEmpty() && !api_key.isEmpty()){
            return new API(api_host, api_key);
        } else {
            log(1, "Invalid api host: " + api_host + " and api key: " + api_key);
            return null;
        }
    }

    private DatabaseConnector initDatabaseConnector(){
        String user = config.getString("database.user");
        String password = config.getString("database.password");
        String url = config.getString("database.url");

        return new DatabaseConnector(url, user, password, this);
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

    private void loadConfigsSerializers(){
        ConfigurationSerialization.registerClass(Message.class);
        ConfigurationSerialization.registerClass(ReportOption.class);
    }

    public void sendReportToStaffs(Report report, boolean sendToChannel){
        if(isRedisBungeeAvailable() && sendToChannel){
            String json = this.gson.toJson(report);
            getRedisBungeeAPI().sendChannelMessage("pandora:report", json);
            return;
        }
        for(IPlayer player : this.getPlayers("pandoracrp.staff.notify")){
            List<String> message = this.getConfig().getStringList("report.messages.sent-to.staffs").stream()
                    .map(
                            x -> x.replace("%reporter%", report.getSender())
                                    .replace("%target%", report.getTarget())
                                    .replace("%reason%", report.getReason())
                                    .replace("%server%", report.getServer())
                    ).collect(Collectors.toList());

            player.sendMessage(message);
        }

    }
}
