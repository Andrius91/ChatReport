package icu.yogurt.chatreport.task;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.service.PunishmentService;

import java.util.List;

public class PunishmentTask implements Runnable {

    private final PunishmentService service;
    private final String filters;

    public PunishmentTask(ChatReport plugin) {
        this.service = plugin.getPunishmentService();
        List<String> filtersList = plugin.getPunishmentConfig().getStringList("punishment.events.task");
        filters =  "?types=" + String.join(",", filtersList);
    }

    @Override
    public void run() {
        service.getPunishments(filters).thenAcceptAsync(punishments -> {
           if(punishments != null && !punishments.isEmpty()){
               punishments.forEach(service::updatePunishment);
           }
        });
    }

}