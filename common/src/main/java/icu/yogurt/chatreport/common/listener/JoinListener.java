package icu.yogurt.chatreport.common.listener;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.cache.UserCache;
import icu.yogurt.chatreport.common.config.Config;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.interfaces.IStorage;
import icu.yogurt.chatreport.common.model.User;
import icu.yogurt.chatreport.common.service.PunishmentService;
import icu.yogurt.chatreport.common.storage.YamlStorage;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Collections;
import java.util.List;

import static icu.yogurt.chatreport.common.ConfigKeys.TYPES_ON_JOIN;

public abstract class JoinListener {
    private final BasePlugin plugin;
    private final UserCache userCache;
    private final String filters;
    private final PunishmentService service;

    public JoinListener(BasePlugin plugin) {
        this.plugin = plugin;
        this.userCache = plugin.getStorageManager().getUserCache();
        this.service = plugin.getPunishmentService();
        List<String> filtersList = TYPES_ON_JOIN.getAsStringList();
        this.filters =  "&types=" + String.join(",", filtersList);
    }

    public void process(IPlayer player) {
        String playerName = player.getUsername();
        String currentUUID = player.getUUID().toString();

        plugin.runAsync(() -> {
            IStorage storage = plugin.getStorage();

            // Check if player has punishment
            checkIfPlayerHasPunishment(playerName);

            if (storage instanceof YamlStorage) {
                YamlFile config = Config.getPlayerConfig(playerName);

                if (config == null) {
                    config = Config.createPlayerConfig(plugin, playerName);
                    config.set("messages", Collections.emptyList());
                }

                config.set("uuid", currentUUID);
                Config.reloadPlayerConfig(playerName);
            } else {
                User cachedUser = userCache.getCachedUserModel(playerName);
                if (cachedUser == null) {
                    User user = new User(playerName, currentUUID, null);
                    userCache.cacheUserModel(playerName, user);

                    // Create user in the db
                    plugin.getDatabase().createUser(user);
                }
            }
        });
    }

    private void checkIfPlayerHasPunishment(String playerName){
        String filters = "?target=" + playerName + this.filters;
        if(plugin.getApi() == null){
            plugin.log(3, "Failed to connect to api: |" + filters+"|");
            return;
        }
        service.getPunishments(filters).thenAcceptAsync(punishments -> {
            if(punishments != null && !punishments.isEmpty()){
                punishments.forEach(plugin.getPunishmentService()::updatePunishment);
            }
        });
    }
}
