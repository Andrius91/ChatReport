package icu.yogurt.chatreport.common.commands;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;

import static icu.yogurt.chatreport.common.ConfigKeys.NO_MESSAGES_FOUND;
import static icu.yogurt.chatreport.common.ConfigKeys.SUCCESS_REPORT;

public class ChatReportCommand extends BaseCommand{

    public ChatReportCommand(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getCommandName() {
        return "chat-report";
    }

    @Override
    protected boolean argsValid(String[] args) {
        return args.length != 1;
    }


    @Override
    protected void executeAsync(IPlayer sender, String[] args) {

        if(!plugin.getStorage().playerHasMessages(target)){
            sender.sendMessage(NO_MESSAGES_FOUND);
            return;
        }

        sender.sendMessage(SUCCESS_REPORT);
        plugin.getChatReportService().createNewReport(senderName, target);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

    }
}
