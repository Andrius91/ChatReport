package icu.yogurt.chatreport.common.commands;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;

public class ChatReportCommand extends BaseCommand{

    private final String NO_MESSAGES_FOUND;
    public ChatReportCommand(BasePlugin plugin) {
        super(plugin);
        this.NO_MESSAGES_FOUND = plugin.getLangConfig().getString("lang.no-messages-found");
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
        plugin.createNewReport(senderName, target);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

    }
}
