package icu.yogurt.chatreport.common.managers;

import icu.yogurt.chatreport.common.API;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.task.ActivePlayerPunishmentTask;
import icu.yogurt.chatreport.common.task.PunishmentProcessingTask;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

import static icu.yogurt.chatreport.common.ConfigKeys.AUTO_ON_JOIN;
import static icu.yogurt.chatreport.common.ConfigKeys.AUTO_TASK;

@RequiredArgsConstructor
public class TaskManager {
    private final BasePlugin plugin;
    private final API api;

    public void loadTasks() {
        boolean auto_task = AUTO_TASK.getAsBoolean();
        boolean auto_on_join = AUTO_ON_JOIN.getAsBoolean();

        if(api == null){
            plugin.log(1, "Failed to connect to api, shutting down tasks...");
            return;
        }

        if(auto_task){
            plugin.scheduledTask(new PunishmentProcessingTask(plugin), 5L, 5L, TimeUnit.SECONDS);
        }
        if(auto_on_join){
            plugin.scheduledTask(new ActivePlayerPunishmentTask(plugin), 5L, 5L, TimeUnit.SECONDS);
        }
    }
}
