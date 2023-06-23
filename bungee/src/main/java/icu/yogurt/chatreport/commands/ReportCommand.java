package icu.yogurt.chatreport.commands;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.chatreport.common.model.CRCommand;
import icu.yogurt.chatreport.common.model.Report;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ReportCommand extends BaseCommand {

    private final String HEADER;
    private final String MAX_CHARS;

    public ReportCommand(ChatReport plugin, CRCommand command) {
        super(plugin, command);
        HEADER = plugin.getConfig().getString("report.header");
        MAX_CHARS = plugin.getLangConfig().getString("lang.max-chars");
    }

    @Override
    protected boolean argsValid(String[] args) {
        return args.length < 1;
    }

    @Override
    protected void executeAsync(CommandSender sender, String[] args) {
        if(args.length == 1){
            PlayerUtils.sendPlayerMessage(sender, HEADER);
            for(TextComponent component : PlayerUtils.reportOptions(plugin, target)){
                player.sendMessage(component);
            }
            return;
        }

        StringBuilder reason = new StringBuilder();
        Report report = new Report();
        String server = player.getServer().getInfo().getName();

        report.setTarget(target);
        report.setSender(senderName);
        report.setServer(server);
        // Get all strings
        for (int i = 1; i < args.length; i++){
            reason.append(args[i]).append(" ");
        }

        String reasonStr = reason.toString().trim();
        if(reasonStr.length() > 36){
            PlayerUtils.sendPlayerMessage(senderName, MAX_CHARS);
            return;
        }

        report.setReason(reasonStr);

        PlayerUtils.sendPlayerMessage(sender, SUCCESS_REPORT);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

        if(plugin.isRedisBungee()){
            String json = plugin.gson.toJson(report);
            RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage("pandora:report", json);
        }else{
            PlayerUtils.sendReportToStaffs(plugin, report);
        }

    }
}
