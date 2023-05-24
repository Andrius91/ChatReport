package icu.yogurt.common.listener;

import icu.yogurt.common.interfaces.IChatReport;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class StaffListener extends JedisPubSub {
    private final IChatReport plugin;


    @Override
    public void onMessage(String channel, String message) {
        // Get username and UUID from message
        String[] parts = message.split(":");
        String username = parts[0];
        String uuid = parts[1];

        // Update the local map with the updated UUID
        plugin.getStorage().getStaffMap().put(username, uuid);
    }
}
