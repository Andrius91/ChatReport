package icu.yogurt.chatreport;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.saicone.ezlib.Dependencies;
import com.saicone.ezlib.Dependency;
import com.saicone.ezlib.EzlibLoader;
import icu.yogurt.chatreport.commands.ChatReportCommand;
import icu.yogurt.chatreport.commands.ReportCommand;
import icu.yogurt.chatreport.listeners.PlayerChatListener;
import icu.yogurt.chatreport.listeners.PlayerJoinListener;
import icu.yogurt.chatreport.listeners.ReportListener;
import icu.yogurt.chatreport.task.PunishmentTask;
import icu.yogurt.common.API;
import icu.yogurt.common.config.Config;
import icu.yogurt.common.connector.DatabaseConnector;
import icu.yogurt.common.connector.RedisConnector;
import icu.yogurt.common.interfaces.IChatReport;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.CRCommand;
import icu.yogurt.common.storage.RedisStorage;
import icu.yogurt.common.storage.YamlStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Dependencies(
        value = {
                // Jedis
                @Dependency(value = "redis.clients:jedis:4.3.0"),
                @Dependency(value = "org.slf4j:slf4j-nop:1.7.36"), // For slf4j-api
                // Simple YAML
                @Dependency(value = "me.carleslc.Simple-YAML:Simple-Yaml:1.8.4",
                        relocate = {"org.simpleyaml", "{package}.libs.yaml"}),
                // OkHttp3
                @Dependency(value = "com.squareup.okhttp3:okhttp:4.2.2", relocate = {
                        "com.squareup", "{package}.libs.okhttp3"
                })
        },
        relocations = {
                // Jedis
                "redis.clients.jedis", "{package}.libs.jedis",
                "com.google.gson", "{package}.libs.gson",
                "org.apache.commons.pool2", "{package}.libs.commons.pool2",
                "org.json", "{package}.libs.json",
                "org.slf4j", "{package}.libs.slf4j"
        }
)
@Getter
public final class ChatReport extends Plugin implements IChatReport{

    private RedisConnector redisConnector;
    private Storage storage;
    private DatabaseConnector database;
    private TaskScheduler scheduler;
    private YamlFile config;
    private YamlFile punishmentConfig;
    private YamlFile langConfig;

    private API api;

    @Setter
    private boolean isDebug = false;

    @Getter
    private boolean isRedisBungee = false;

    @Override
    public void onLoad() {
        EzlibLoader loader = new EzlibLoader(new File(getDataFolder(), "libs"));

        loader.replace("{package}", "icu.yogurt");
        loader.load();

        scheduler = ProxyServer.getInstance().getScheduler();
        loadConfigs();

        runAsync(() -> {
            // Storage
            String storage_type = config.getString("storage.type");
            if(storage_type.equalsIgnoreCase("REDIS")){
                String url = config.getString("storage.url");
                redisConnector = new RedisConnector(url);
                storage = new RedisStorage(this, redisConnector);
            }else{
                storage = new YamlStorage();
            }

            // Database
            String user = config.getString("database.user");
            String password = config.getString("database.password");
            String url = config.getString("database.url");

            database = new DatabaseConnector(url, user, password, this);

        });
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        String api_host = getConfig().getString("api.host");
        String api_key = getConfig().getString("api.key");
        isDebug = getConfig().getBoolean("debug");
        isRedisBungee = config.getBoolean("use-redis-bungee");
        boolean auto_punisher = punishmentConfig.getBoolean("punishment.enabled");

        if(!api_host.isEmpty() && !api_key.isEmpty()){
            api = new API(api_host, api_key);
        }else{
            log(2, "Invalid api host: " + api_host + " and api key: " + api_key);
        }

        // Tasks
        if(auto_punisher){
            scheduler.schedule(this, new PunishmentTask(this), 5L, 5L, TimeUnit.SECONDS);
        }
        registerListeners();
        registerCommand();

        if(isRedisBungee){
            registerChannels();
        }
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(redisConnector != null) {
            redisConnector.close(); // Close the Redis connection
        }
        if(isRedisBungee){
            unregisterChannels();
        }
    }

    private void loadConfigs(){
        Config.createFolder(this);
        config = new Config().get(this, "config.yml");
        punishmentConfig = new Config().get(this, "punishment.yml");
        langConfig = new Config().get(this, "lang.yml");
    }

    private void registerListeners(){
        String path = config.getString("storage.type");
        boolean SAVE_MESSAGES = !path.equalsIgnoreCase("DISABLED");

        if(SAVE_MESSAGES){
            registerListener(new PlayerChatListener(this));

        }
        if(isRedisBungee){
            registerListener(new ReportListener(this));
        }

        registerListener(new PlayerJoinListener(this));
    }

    private void registerListener(Listener listener){
        ProxyServer.getInstance().getPluginManager().registerListener(this, listener);
    }

    private void registerChannels(){
        RedisBungeeAPI.getRedisBungeeApi().registerPubSubChannels("pandora:report");
    }

    private void unregisterChannels(){
        RedisBungeeAPI.getRedisBungeeApi().unregisterPubSubChannels("pandora:report");
    }

    private void registerCommand() {

        // ChatReport
        CRCommand crCommand = getCRCommand("chat-report");

        ProxyServer.getInstance().getPluginManager()
                .registerCommand(this, new ChatReportCommand(this, crCommand));

        // Report
        CRCommand rCommand = getCRCommand("report");

        ProxyServer.getInstance().getPluginManager()
                .registerCommand(this, new ReportCommand(this, rCommand));
    }

    private CRCommand getCRCommand(String command){
        String name = config.getString("commands."+command+".command");
        String usage = config.getString("commands."+command+".usage");
        String permission = config.getString("commands."+command+".permission");
        List<String> aliases = config.getStringList("commands."+command+".aliases");

        return new CRCommand(name, usage, permission, aliases);
    }

    /*
      ----------------------------------------------------------------
                                   IMPLEMENTS
      ----------------------------------------------------------------
     */

    /**
     * Method to execute an asynchronous task in BungeeCord using the scheduler.
     * @param runnable The asynchronous task to execute.
     */
    @Override
    public void runAsync(Runnable runnable) {
        // Use the runAsync() method of the scheduler to execute the task in a separate thread
        scheduler.runAsync(this, runnable);
    }

    @Override
    public void executeCommand(String command){
        ProxyServer.getInstance().getPluginManager()
                .dispatchCommand(ProxyServer.getInstance().getConsole(), command);
    }

    @Override
    public List<String> getPlayersList(String server) {
        List<String> playersList;
        if(isRedisBungee){
            playersList = RedisBungeeAPI.getRedisBungeeApi().getPlayersOnServer(server)
                    .stream().map(x -> RedisBungeeAPI.getRedisBungeeApi().getNameFromUuid(x))
                    .collect(Collectors.toList());
        }else{
            playersList = ProxyServer.getInstance().getServerInfo(server).getPlayers()
                    .stream()
                    .map(CommandSender::getName)
                    .collect(Collectors.toList());
        }

        return playersList;
    }

    @Override
    public void log(int level, String message) {
        switch (level) {
            case 1:
                getLogger().log(Level.SEVERE, message);
                break;
            case 2:
                getLogger().log(Level.WARNING, message);
                break;
            case 3:
                if(isDebug){
                    getLogger().log(Level.INFO, message);
                }
                break;
            default:
                getLogger().log(Level.INFO, message);
                break;
        }
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public API getApi() {
        return api;
    }

    public YamlFile getConfig(){
        return config;
    }

}
