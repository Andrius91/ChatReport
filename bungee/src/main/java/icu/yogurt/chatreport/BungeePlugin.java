package icu.yogurt.chatreport;

import icu.yogurt.chatreport.commands.BungeeCommand;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.ConfigKeys;
import icu.yogurt.chatreport.common.commands.ChatReportCommand;
import icu.yogurt.chatreport.common.commands.ReportCommand;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.impl.BungeePlayer;
import icu.yogurt.chatreport.listeners.PlayerChatListener;
import icu.yogurt.chatreport.listeners.PlayerJoinListener;
import icu.yogurt.chatreport.listeners.ReportPubSubListener;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static icu.yogurt.chatreport.common.ConfigKeys.USE_REDIS_BUNGEE;

public class BungeePlugin extends BasePlugin {

    private final ChatReport plugin;

    public BungeePlugin(ChatReport plugin){
        this.plugin = plugin;
    }

    private ProxyServer proxy;
    private TaskScheduler scheduler;
    private BungeeAudiences audienceProvider;

    @Override
    public void onLoad(){
        this.proxy = ProxyServer.getInstance();
        this.scheduler = proxy.getScheduler();

        super.onLoad();
    }

    @Override
    protected void onEnable(){
        this.audienceProvider = BungeeAudiences.create(plugin);
        super.onEnable();
    }


    @Override
    protected void onDisable(){
        super.onDisable();
    }

    @Override
    protected void registerCommands(){
        // ChatReport
        ChatReportCommand chatReportCommand = new ChatReportCommand(this);

        proxy.getPluginManager()
                .registerCommand(plugin, new BungeeCommand(this, chatReportCommand));

        // Report
        ReportCommand reportCommand = new ReportCommand(this);
        proxy.getPluginManager()
                .registerCommand(plugin, new BungeeCommand(this, reportCommand));
    }

    @Override
    protected void registerListeners(){
        boolean SAVE_MESSAGES = ConfigKeys.SAVE_MESSAGES.getAsBoolean();

        if(SAVE_MESSAGES){
            registerListener(new PlayerChatListener(this));
        }
        if(isRedisBungeeAvailable()){
            registerListener(new ReportPubSubListener(this));
        }

        registerListener(new PlayerJoinListener(this));
    }

    @Override
    public AudienceProvider getAudienceProvider() {
        if(this.audienceProvider == null){
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        }
        return this.audienceProvider;
    }


    @Override
    public boolean isRedisBungeeAvailable(){
        boolean usingRedisBungee = USE_REDIS_BUNGEE.getAsBoolean();
        return ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null && usingRedisBungee;
    }


    @Override
    public void log(int level, String message){
        switch (level){
            case 1:
                plugin.getLogger().log(Level.SEVERE, message);
                break;
            case 2:
                plugin.getLogger().log(Level.WARNING, message);
                break;
            case 3:
                if(isDebug()){
                    plugin.getLogger().log(Level.INFO, message);
                }
                break;
            default:
                plugin.getLogger().log(Level.INFO, message);
                break;
        }
    }

    @Override
    public File getDataFolder() {
        return plugin != null ? plugin.getDataFolder() : new File("");
    }


    @Override
    public void runAsync(Runnable runnable) {
        scheduler.runAsync(plugin, runnable);
    }

    @Override
    public void scheduledTask(Runnable runnable, long delay, long period, TimeUnit timeUnit){
        scheduler.schedule(plugin, runnable, delay, period, timeUnit);
    }

    @Override
    public void executeCommand(String command) {
        proxy.getPluginManager().dispatchCommand(proxy.getConsole(), command);
    }

    @Override
    public IPlayer getPlayerByUsername(String username) {
        ProxiedPlayer proxiedPlayer = proxy.getPlayer(username);
        return (proxiedPlayer != null) ? new BungeePlayer(this, proxiedPlayer) : null;
    }

    @Override
    public List<String> getPlayersList(String server) {
        List<String> playersList;
        if(isRedisBungeeAvailable()){
            playersList = getRedisBungeeAPI().getPlayersOnServer(server)
                    .stream().map(uuid -> getRedisBungeeAPI().getNameFromUuid(uuid))
                    .collect(Collectors.toList());
        } else {
            playersList = proxy.getServerInfo(server).getPlayers()
                    .stream()
                    .map(CommandSender::getName)
                    .collect(Collectors.toList());
        }

        return playersList;
    }

    @Override
    public List<IPlayer> getPlayers(String permission){
        return proxy.getPlayers().stream()
                .filter(proxiedPlayer -> proxiedPlayer.hasPermission(permission))
                .map(proxiedPlayer -> new BungeePlayer(this, proxiedPlayer))
                .collect(Collectors.toList());
    }

    private void registerListener(Listener listener){
        proxy.getPluginManager().registerListener(plugin, listener);
    }


}
