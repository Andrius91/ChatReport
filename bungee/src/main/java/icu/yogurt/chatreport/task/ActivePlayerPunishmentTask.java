package icu.yogurt.chatreport.task;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.model.Punishment;
import icu.yogurt.common.service.PunishmentService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class ActivePlayerPunishmentTask implements Runnable {

    private final PunishmentService service;
    private final String JOIN_FILTERS;
    private int page;

    public ActivePlayerPunishmentTask(ChatReport plugin) {
        this.service = plugin.getPunishmentService();
        List<String> joinFiltersList = plugin.getPunishmentConfig().getStringList("punishment.types.on-join");
        JOIN_FILTERS = "?types=" + String.join(",", joinFiltersList);
    }

    @Override
    public void run() {
        service.getPunishments(JOIN_FILTERS, page).thenAcceptAsync(punishments -> {
            if (!punishments.isEmpty()) {
                for(Punishment punishment: punishments){
                    String target = punishment.getTarget();
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);

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
