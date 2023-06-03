package icu.yogurt.chatreport.commands;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.common.model.CRCommand;
import net.md_5.bungee.api.CommandSender;

public class ChatReportCommand extends BaseCommand {

    public ChatReportCommand(ChatReport plugin, CRCommand command) {
        super(plugin, command);
    }

    @Override
    protected boolean argsValid(String[] args) {
        return args.length != 1;
    }

    @Override
    protected void executeAsync(CommandSender sender, String[] args) {
        PlayerUtils.sendPlayerMessage(sender, SUCCESS_REPORT);
        plugin.createNewReport(senderName, target);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

    }
}

