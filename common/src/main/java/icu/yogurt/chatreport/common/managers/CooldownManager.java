package icu.yogurt.chatreport.common.managers;

import icu.yogurt.chatreport.common.BasePlugin;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final BasePlugin plugin;

    private final HashMap<UUID, Long> cooldownMap;
    public CooldownManager(BasePlugin plugin){
        this.plugin = plugin;
        this.cooldownMap = new HashMap<>();
    }


    public long getTimeOfPlayerInMilis(UUID uuid){
        if(cooldownMap.containsKey(uuid)){
            return cooldownMap.get(uuid);
        }
        return 0L;
    }

    public void addPlayer(UUID uuid){
        cooldownMap.put(uuid, System.currentTimeMillis());
    }
    public boolean hasCooldown(UUID uuid){
        return System.currentTimeMillis() - getTimeOfPlayerInMilis(uuid) < getCooldown();
    }

    public int getTimeLeft(UUID uuid){
        int time_left = (int)(System.currentTimeMillis() - getTimeOfPlayerInMilis(uuid)
                - getCooldown()) / 1000;
        time_left = Math.abs(time_left);

        return time_left;
    }

    public String getTimeLeftStr(UUID uuid){
        return String.valueOf(getTimeLeft(uuid));
    }
    public long getCooldown(){
        return plugin.getConfig().getLong("report.cooldown") * 1000;
    }
}
