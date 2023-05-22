package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.config.Config;
import icu.yogurt.common.interfaces.Storage;
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
            String path = plugin.getConfig().getString("storage.type");
            Storage storage = plugin.getStorage();
            boolean isStaff = player.hasPermission("pandora.staff");
            boolean SAVE_PLAYERS = !path.equalsIgnoreCase("DISABLED");
            if(storage instanceof YamlStorage && SAVE_PLAYERS){
                YamlFile config = Config.getPlayerConfig(playerName);

                if(config == null){
                    config = Config.createPlayerConfig(plugin, playerName);
                    config.set("messages", Collections.emptyList());
                }

                config.set("uuid", player.getUniqueId().toString());

                Config.reloadPlayerConfig(playerName);
            }

            if(isStaff){
                String uuid = plugin.getStorage().getStaffUUID(playerName);
                if(!currentUUID.equals(uuid)){
                    plugin.getStorage().updateStaffUUID(playerName, currentUUID);
                }
            }
        });

    }
}
