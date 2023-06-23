package icu.yogurt.chatreport.commands;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.chatreport.common.model.CRCommand;
import net.md_5.bungee.api.CommandSender;

public class ChatReportCommand extends BaseCommand {

    private final String NO_MESSAGES_FOUND;
    public ChatReportCommand(ChatReport plugin, CRCommand command) {
        super(plugin, command);
        this.NO_MESSAGES_FOUND = plugin.getLangConfig().getString("lang.no-messages-found");
    }

    @Override
    protected boolean argsValid(String[] args) {
        return args.length != 1;
    }

    @Override
    protected void executeAsync(CommandSender sender, String[] args) {

        if(!plugin.getStorage().playerHasMessages(target)){
            PlayerUtils.sendPlayerMessage(sender, NO_MESSAGES_FOUND);
            return;
        }

        PlayerUtils.sendPlayerMessage(sender, SUCCESS_REPORT);
        plugin.createNewReport(senderName, target);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

    }
}

