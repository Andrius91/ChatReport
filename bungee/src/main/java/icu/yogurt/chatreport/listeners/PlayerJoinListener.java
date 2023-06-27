package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.listener.JoinListener;
import icu.yogurt.chatreport.impl.BungeePlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerJoinListener extends JoinListener implements Listener {

    private final BasePlugin plugin;

    public PlayerJoinListener(BasePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PostLoginEvent e){

        BungeePlayer bungeePlayer = (BungeePlayer) plugin.getPlayerByUsername(e.getPlayer().getName());
        super.process(bungeePlayer);

    }

}
