package icu.yogurt.chatreport.common.task;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.service.PunishmentService;

import java.util.List;

import static icu.yogurt.chatreport.common.ConfigKeys.TYPES_TASK;

public class PunishmentProcessingTask implements Runnable {

    private final PunishmentService service;
    private final String TASK_FILTERS;

    public PunishmentProcessingTask(BasePlugin plugin) {
        this.service = plugin.getPunishmentService();
        List<String> taskFiltersList = TYPES_TASK.getAsStringList();
        TASK_FILTERS =  "?types=" + String.join(",", taskFiltersList);
    }

    @Override
    public void run() {
        service.getPunishments(TASK_FILTERS).thenAcceptAsync(punishments -> {
            if (punishments != null && !punishments.isEmpty()) {
                punishments.forEach(service::updatePunishment);
            }
        });
    }

}