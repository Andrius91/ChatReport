package icu.yogurt.chatreport.common.interfaces;

import icu.yogurt.chatreport.common.config.ConfigKey;

import java.util.List;
import java.util.UUID;

public interface IPlayer {

    String getUsername();
    void sendMessage(String message);
    void sendMessage(ConfigKey<String> configKey);
    void sendMessage(List<String> messages);
    boolean isConnected();
    UUID getUUID();
    String getCurrentServerName();
    boolean hasPermission(String permission);

}
