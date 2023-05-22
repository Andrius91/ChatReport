package icu.yogurt.chatreport.listeners;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.model.Message;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;


public class PlayerChatListener implements Listener {

    private final ChatReport plugin;
    private final List<String> COMMANDS_LIST;
    private final boolean SAVE_COMMANDS;

    public PlayerChatListener(ChatReport plugin){
        this.plugin = plugin;
        this.COMMANDS_LIST = plugin.getConfig().getStringList("messages.commands");
        this.SAVE_COMMANDS = plugin.getConfig().getBoolean("messages.save-commands");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent e){
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        String playerMessage = e.getMessage();
        String playerName = player.getName();
        String server = player.getServer().getInfo().getName();


        if(SAVE_COMMANDS){
            if(e.isCommand() || e.isProxyCommand()){
                String command = e.getMessage().split(" ")[0].replace("/", "").trim();
                if(!COMMANDS_LIST.contains(command)){
                    return;
                }
            }
        }

        plugin.runAsync( () -> {
            Message message = new Message(playerMessage, server, playerName, Message.nowDate());
            plugin.getStorage().saveMessage(playerName, message);
        });
    }
}