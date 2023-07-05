package icu.yogurt.chatreport.common.task;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.model.Punishment;
import icu.yogurt.chatreport.common.service.PunishmentService;

import java.util.List;

import static icu.yogurt.chatreport.common.ConfigKeys.TYPES_ON_JOIN;

public class ActivePlayerPunishmentTask implements Runnable {

    private final BasePlugin plugin;
    private final PunishmentService service;
    private final String JOIN_FILTERS;
    private int page;

    public ActivePlayerPunishmentTask(BasePlugin plugin) {
        this.service = plugin.getPunishmentService();
        this.plugin = plugin;
        List<String> joinFiltersList = TYPES_ON_JOIN.getAsStringList();
        JOIN_FILTERS = "?types=" + String.join(",", joinFiltersList);
    }

    @Override
    public void run() {
        service.getPunishments(JOIN_FILTERS, page).thenAcceptAsync(punishments -> {
            if (!punishments.isEmpty()) {
                for(Punishment punishment: punishments){
                    String target = punishment.getTarget();
                    IPlayer targetPlayer = plugin.getPlayerByUsername(target);

                    if(targetPlayer != null && targetPlayer.isConnected()){
                        service.updatePunishment(punishment);
                    }
                }
                if (punishments.size() >= 20) {
                    this.page++;
                }
            } else {
                this.page = 0;
            }
        });
    }
}
