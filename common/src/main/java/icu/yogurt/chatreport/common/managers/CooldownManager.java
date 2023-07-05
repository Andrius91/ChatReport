package icu.yogurt.chatreport.common.managers;

import java.util.HashMap;
import java.util.UUID;

import static icu.yogurt.chatreport.common.ConfigKeys.REPORT_COOLDOWN;

public class CooldownManager {


    private final HashMap<UUID, Long> cooldownMap;
    public CooldownManager(){
        this.cooldownMap = new HashMap<>();
    }


    public long getTimeOfPlayerInMillis(UUID uuid){
        if(cooldownMap.containsKey(uuid)){
            return cooldownMap.get(uuid);
        }
        return 0L;
    }

    public void addPlayer(UUID uuid){
        cooldownMap.put(uuid, System.currentTimeMillis());
    }
    public boolean hasCooldown(UUID uuid){
        return System.currentTimeMillis() - getTimeOfPlayerInMillis(uuid) < getCooldown();
    }

    public int getTimeLeft(UUID uuid){
        int time_left = (int)(System.currentTimeMillis() - getTimeOfPlayerInMillis(uuid)
                - getCooldown()) / 1000;
        time_left = Math.abs(time_left);

        return time_left;
    }

    public String getTimeLeftStr(UUID uuid){
        return String.valueOf(getTimeLeft(uuid));
    }
    public long getCooldown(){
        return REPORT_COOLDOWN.getAsInteger() * 1000;
    }
}
