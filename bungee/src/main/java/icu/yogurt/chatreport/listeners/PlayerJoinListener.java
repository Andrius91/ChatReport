package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.config.Config;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.UserModel;
import icu.yogurt.common.storage.YamlStorage;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Collections;

@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final ChatReport plugin;

    @EventHandler
    public void onJoin(PostLoginEvent e){

        plugin.runAsync(() -> {
            ProxiedPlayer player = e.getPlayer();
            String playerName = e.getPlayer().getName();
            String currentUUID = e.getPlayer().getUniqueId().toString();
            Storage storage = plugin.getStorage();
            if(storage instanceof YamlStorage){
                YamlFile config = Config.getPlayerConfig(playerName);

                if(config == null){
                    config = Config.createPlayerConfig(plugin, playerName);
                    config.set("messages", Collections.emptyList());
                }

                config.set("uuid", player.getUniqueId().toString());

                Config.reloadPlayerConfig(playerName);
            }

            // Create user in the db
            UserModel userModel = new UserModel(playerName, currentUUID, null);
            plugin.getDatabase().createUser(userModel);
        });

    }
}
