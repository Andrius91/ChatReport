package icu.yogurt.chatreport.common.service;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.config.Replacer;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.model.Report;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static icu.yogurt.chatreport.common.ConfigKeys.MESSAGES_SENT_TO_STAFFS;

@RequiredArgsConstructor
public class ReportService {
    private final BasePlugin plugin;

    public void createReport(Report report){
        sendReportToStaffs(report, true);
    }

    public void sendReportToStaffs(Report report, boolean sendToChannel){
        if(plugin.isRedisBungeeAvailable() && sendToChannel){
            String json = plugin.gson.toJson(report);
            plugin.getRedisBungeeAPI().sendChannelMessage("pandora:report", json);
            return;
        }
        for(IPlayer player : plugin.getPlayers("pandoracrp.staff.notify")){
            Replacer replacer = new Replacer();
            replacer.addReplacement("%reporter%", report.getSender());
            replacer.addReplacement("%target%", report.getTarget());
            replacer.addReplacement("%reason%", report.getReason());
            replacer.addReplacement("%server%", report.getServer());

            List<String> message = MESSAGES_SENT_TO_STAFFS.replace(replacer).getAsStringList();

            player.sendMessage(message);
        }
    }
}
