package icu.yogurt.chatreport.common.task;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.model.Punishment;
import icu.yogurt.chatreport.common.service.PunishmentService;

import java.util.List;

public class ActivePlayerPunishmentTask implements Runnable {

    private final BasePlugin plugin;
    private final PunishmentService service;
    private final String JOIN_FILTERS;
    private int page;

    public ActivePlayerPunishmentTask(BasePlugin plugin) {
        this.service = plugin.getPunishmentService();
        this.plugin = plugin;
        List<String> joinFiltersList = plugin.getPunishmentConfig().getStringList("punishment.types.on-join");
        JOIN_FILTERS = "?types=" + String.join(",", joinFiltersList);
    }

    @Override
    public void run() {
        if(plugin.getApi() == null){
            return;
        }

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
