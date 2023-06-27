package icu.yogurt.chatreport.common.task;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.service.PunishmentService;

import java.util.List;

public class PunishmentProcessingTask implements Runnable {

    private final BasePlugin plugin;
    private final PunishmentService service;
    private final String TASK_FILTERS;

    public PunishmentProcessingTask(BasePlugin plugin) {
        this.plugin = plugin;
        this.service = plugin.getPunishmentService();
        List<String> taskFiltersList = plugin.getPunishmentConfig().getStringList("punishment.types.task");
        TASK_FILTERS =  "?types=" + String.join(",", taskFiltersList);
    }

    @Override
    public void run() {
        if(plugin.getApi() == null){
            return;
        }
        service.getPunishments(TASK_FILTERS).thenAcceptAsync(punishments -> {
            if (punishments != null && !punishments.isEmpty()) {
                punishments.forEach(service::updatePunishment);
            }
        });
    }

}