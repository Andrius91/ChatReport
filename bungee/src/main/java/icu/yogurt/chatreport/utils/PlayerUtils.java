package icu.yogurt.chatreport.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerUtils {

    public static BaseComponent[] color(String txt) {
        if (txt == null) {
            throw new NullPointerException("String empty");
        }
        return new ComponentBuilder(textColor(txt)).create();
    }

    public static void sendPlayerMessage(CommandSender p, String message){
        p.sendMessage(color(message));
    }
    public static void sendPlayerMessage(String sender, String message){
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(sender);
        p.sendMessage(color(message));
    }

    public static String textColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
