package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.listener.ChatListener;
import icu.yogurt.chatreport.impl.BungeePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


public class PlayerChatListener extends ChatListener implements Listener {

    private final BasePlugin plugin;

    public PlayerChatListener(BasePlugin plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent e) {
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        BungeePlayer bungeePlayer = (BungeePlayer) plugin.getPlayerByUsername(player.getName());
        String playerMessage = e.getMessage();
        boolean isCommand = e.isCommand() || e.isProxyCommand();

        super.process(bungeePlayer, playerMessage, isCommand);

    }
}
