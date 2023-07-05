package icu.yogurt.chatreport.common.listener;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.model.Report;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RedisListener {

    private final BasePlugin plugin;

    public void process(String reportJson) {
        Report report = plugin.gson.fromJson(reportJson, Report.class);
        if (report != null) {
            plugin.getReportService().sendReportToStaffs(report, false);
        }
    }
}
