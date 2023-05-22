package icu.yogurt.chatreport.task;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.common.model.Punishment;
import icu.yogurt.common.model.Report;
import icu.yogurt.common.model.enums.PunishStatus;
import icu.yogurt.common.model.enums.PunishType;
import icu.yogurt.common.model.enums.PunishUnit;
import org.simpleyaml.configuration.file.YamlFile;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentTask implements Runnable {

    private final ChatReport plugin;
    private final YamlFile config;
    private final Gson gson;
    private final Type punishmentListType;

    public PunishmentTask(ChatReport plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.gson = new Gson();
        punishmentListType = new TypeToken<List<Punishment>>(){}.getType();
    }

    @Override
    public void run() {
        CompletableFuture<String> result = plugin.getApi().getAsync("/api/punishments");

        result.thenAcceptAsync(x -> {
                    List<Punishment> punishmentList;
                    try {
                        punishmentList = gson.fromJson(x, punishmentListType);
                    } catch (JsonSyntaxException e) {
                        plugin.log(1, "Failed to parse JSON: " + e.getMessage());
                        return;
                    }

                    if (punishmentList != null) {
                        punishmentList.forEach(punishment -> {
                            Punishment punishUpdated = new Punishment();
                            punishUpdated.setStatus(PunishStatus.PUNISHED);
                            String json = gson.toJson(punishUpdated);
                            int punishId = punishment.getPunishmentId();
                            plugin.getApi().updateAsync("/api/punishments/" + punishId, json)
                                    .thenAcceptAsync(updated -> {
                                        String command = getPunishmentCommand(punishment);

                                        plugin.executeCommand(command);
                                    })
                                    .exceptionally(ex -> {
                                        plugin.log(1, "Failed to update punishment: " + ex.getMessage());
                                        return null;
                                    });
                        });
                    }
                })
                .exceptionally(ex -> {
                    plugin.log(1, "Failed to get punishments: " + ex.getMessage());
                    return null;
                });
    }

    private String getPunishmentCommand(Punishment punishment) {
        PunishType type = punishment.getType();
        Report report = punishment.getReport();

        String staff = punishment.getStaff().getUsername();
        String staffUuid = plugin.getStorage().getStaffUUID(staff);

        String target = punishment.getTarget();
        String reason = punishment.getReason();
        String id = report != null ? report.getReportId() : "";

        String timeValue = punishment.getTimeValue().toString();
        PunishUnit unit = punishment.getTimeUnit();
        String unitString = getUnit(unit);
        String time = timeValue + unitString;

        int index = report == null ? 0 : 1;

        List<String> commandsList = config.getStringList("punishment.commands." + type.name());
        String path = commandsList.get(index);

        StringBuilder sb = new StringBuilder(path);
        replacePlaceholder(sb, "%target%", target);
        replacePlaceholder(sb, "%reason%", reason);
        replacePlaceholder(sb, "%time%", time);
        replacePlaceholder(sb, "%staff%", staff);
        replacePlaceholder(sb, "%staff_uuid%", staffUuid);
        replacePlaceholder(sb, "%id%", id);

        System.out.println("staffUuid = " + staffUuid);
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
