package icu.yogurt.chatreport.common.interfaces;

import java.util.List;
import java.util.UUID;

public interface IPlayer {

    String getUsername();
    void sendMessage(String message);
    void sendMessage(List<String> messages);
    boolean isConnected();
    UUID getUUID();
    String getCurrentServerName();
    boolean hasPermission(String permission);

}
