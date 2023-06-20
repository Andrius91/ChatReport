package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.cache.UserCache;
import icu.yogurt.common.config.Config;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.UserModel;
import icu.yogurt.common.service.PunishmentService;
import icu.yogurt.common.storage.YamlStorage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Collections;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private final ChatReport plugin;
    private final UserCache userCache;
    private final String filters;
    private final PunishmentService service;

    public PlayerJoinListener(ChatReport plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getUserCache();
        this.service = plugin.getPunishmentService();
        List<String> filtersList = plugin.getPunishmentConfig().getStringList("punishment.events.on-join");
        this.filters =  "&types=" + String.join(",", filtersList);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PostLoginEvent e){

        plugin.runAsync(() -> {
            ProxiedPlayer player = e.getPlayer();
            String playerName = player.getName();
            String currentUUID = player.getUniqueId().toString();
            Storage storage = plugin.getStorage();

            // Check if player has punishment
            checkIfPlayerHasPunishment(playerName);

            if(storage instanceof YamlStorage){
                YamlFile config = Config.getPlayerConfig(playerName);

                if(config == null){
                    config = Config.createPlayerConfig(plugin, playerName);
                    config.set("messages", Collections.emptyList());
                }

                config.set("uuid", currentUUID);

                Config.reloadPlayerConfig(playerName);

                return;
            }

            // Cache
            UserModel cachedUserModel = userCache.getCachedUserModel(playerName);
            if (cachedUserModel != null) {
                return;
            }

            UserModel userModel = new UserModel(playerName, currentUUID, null);
            userCache.cacheUserModel(playerName, userModel);

            // Create user in the db
            plugin.getDatabase().createUser(userModel);

        });

    }

    private void checkIfPlayerHasPunishment(String playerName){
        String filters = "?target=" + playerName + this.filters;
        service.getPunishments(filters).thenAcceptAsync(punishments -> {
            if(punishments != null && !punishments.isEmpty()){
                punishments.forEach(service::updatePunishment);
            }
        });
    }
}
