package icu.yogurt.chatreport.listeners;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.chatreport.common.model.Report;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ReportListener implements Listener {

    private final ChatReport plugin;
    private final String channel;
    private final Gson gson;
    public ReportListener(ChatReport plugin){
        this.plugin = plugin;
        this.gson = plugin.gson;
        channel = "pandora:report";
    }

    @EventHandler
    public void pubSub(PubSubMessageEvent e){
        if(!e.getChannel().equals(channel)){
            return;
        }
        Report report = gson.fromJson(e.getMessage(), Report.class);

        if(report == null){
            return;
        }

        PlayerUtils.sendReportToStaffs(plugin, report);

    }
}
