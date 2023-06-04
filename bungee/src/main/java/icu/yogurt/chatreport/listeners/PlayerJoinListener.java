package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.cache.UserCache;
import icu.yogurt.common.config.Config;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.UserModel;
import icu.yogurt.common.storage.YamlStorage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Collections;

public class PlayerJoinListener implements Listener {

    private final ChatReport plugin;
    private final UserCache userCache;

    public PlayerJoinListener(ChatReport plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getUserCache();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PostLoginEvent e){

        plugin.runAsync(() -> {
            ProxiedPlayer player = e.getPlayer();
            String playerName = player.getName();
            String currentUUID = player.getUniqueId().toString();
            Storage storage = plugin.getStorage();
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
}
