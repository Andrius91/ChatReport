package icu.yogurt.chatreport.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.model.ChatReport;
import icu.yogurt.chatreport.common.model.Punishment;
import icu.yogurt.chatreport.common.model.enums.PunishStatus;
import icu.yogurt.chatreport.common.model.enums.PunishType;
import icu.yogurt.chatreport.common.model.enums.PunishUnit;
import org.simpleyaml.configuration.file.YamlFile;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentService {

    private final BasePlugin plugin;
    private final Gson gson;
    private final Type punishmentListType;
    private final YamlFile config;

    public PunishmentService(BasePlugin plugin){
        this.plugin = plugin;
        this.gson = plugin.gson;
        this.punishmentListType = new TypeToken<List<Punishment>>(){}.getType();
        config = plugin.getPunishmentConfig();
    }

    public CompletableFuture<List<Punishment>> getPunishments(String filters, int page) {
        long startTime = System.currentTimeMillis();
        filters = filters + "&page="+ page;
        CompletableFuture<String> result = plugin.getApi().getAsync("/api/punishments" + filters);


        return result.thenApplyAsync(x -> {
            List<Punishment> punishmentList;
            try {
                punishmentList = gson.fromJson(x, punishmentListType);
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                int punishmentSize = punishmentList != null ? punishmentList.size() : 0;
                plugin.log(3, "(punishmentList:" + punishmentSize + ") loaded in "+ elapsedTime + "ms");
            } catch (JsonSyntaxException e) {
                plugin.log(1, "Failed to parse JSON: " + e.getMessage());
                return Collections.emptyList();
            }

            return punishmentList != null ? punishmentList : Collections.emptyList();
        });
    }
    public CompletableFuture<List<Punishment>> getPunishments(String filters){
        return getPunishments(filters, 0);
    }

    public void updatePunishment(Punishment punishment){
        Punishment punishUpdated = new Punishment();
        punishUpdated.setStatus(PunishStatus.PUNISHED);
        punishUpdated.setDate(plugin.nowDate());
        String json = gson.toJson(punishUpdated);
        int punishId = punishment.getPunishmentId();
        long startTime = System.currentTimeMillis();
        plugin.getApi().updateAsync("/api/punishments/" + punishId, json)
                .thenAcceptAsync(updated -> {
                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;
                    plugin.log(3, "(updatePunishment:" + punishId + ") updated in "+ elapsedTime + "ms");
                    String command = getPunishmentCommand(punishment);
                    plugin.executeCommand(command);
                })
                .exceptionally(ex -> {
                    plugin.log(1, "Failed to update punishment: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
    }

    private String getPunishmentCommand(Punishment punishment) {
        PunishType type = punishment.getType();
        ChatReport chatReport = punishment.getChatReport();

        String staff = punishment.getStaff().getUsername();
        String staffUuid = plugin.getStorage().getUserUUID(staff);

        String target = punishment.getTarget();
        String reason = punishment.getReason();
        String id = chatReport != null ? chatReport.getReportId() : "";

        String timeValue = punishment.getTimeValue().toString();
        PunishUnit unit = punishment.getTimeUnit();
        String unitString = getUnit(unit);
        String time = timeValue + unitString;

        String commandKey = chatReport == null ? "no_report_id" : "with_report_id";

        String path = null;
        if(config.contains("punishment.commands." + type.name() + "." + commandKey)) {
            path = config.getString("punishment.commands." + type.name() + "." + commandKey);
        } else if (config.contains("punishment.commands." + type.name() + ".default")) {
            path = config.getString("punishment.commands." + type.name() + ".default");
        }

        if(path == null) {
            throw new RuntimeException("No command found for punishment type: " + type.name());
        }

        StringBuilder sb = new StringBuilder(path);
        replacePlaceholder(sb, "%target%", target);
        replacePlaceholder(sb, "%reason%", reason);
        replacePlaceholder(sb, "%time%", time);
        replacePlaceholder(sb, "%staff%", staff);
        replacePlaceholder(sb, "%staff_uuid%", staffUuid);
        replacePlaceholder(sb, "%id%", id);

        return sb.toString();
    }


    private String getUnit(PunishUnit unit) {
        String unitPath = "punishment.units." + unit.name();
        return config.getString(unitPath);
    }

    private void replacePlaceholder(StringBuilder sb, String placeholder, String value) {
        int index = sb.indexOf(placeholder);
        if (index != -1) {
            sb.replace(index, index + placeholder.length(), value);
        }
    }
}
