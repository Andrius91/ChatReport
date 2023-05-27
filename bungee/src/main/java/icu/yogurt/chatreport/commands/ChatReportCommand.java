package icu.yogurt.chatreport.commands;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.common.model.CRCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import static icu.yogurt.chatreport.commands.ReportCommand.getStrings;

public class ChatReportCommand extends Command implements TabExecutor {
    private final ChatReport plugin;
    private final String PLAYER_NOT_FOUND;
    private final String SUCCESS_REPORT;
    private final String CORRECT_USAGE;
    private final String SELF_REPORT;

    public ChatReportCommand(ChatReport plugin, CRCommand command) {
        super(command.getCommand(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.plugin = plugin;
        PLAYER_NOT_FOUND = plugin.getLangConfig().getString("lang.player-does-not-exist");
        SUCCESS_REPORT = plugin.getLangConfig().getString("lang.success-report");
        CORRECT_USAGE = plugin.getLangConfig().getString("lang.correct-usage")
                .replace("%command_usage%", command.getUsage());
        SELF_REPORT = plugin.getLangConfig().getString("lang.self-report");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if(plugin.isDebug()){
                plugin.log(1, "Debug mode: off.");
                plugin.setDebug(false);
            }else{
                plugin.log(1, "Debug mode: on.");
                plugin.setDebug(true);
            }
            return;
        }

        if(args.length != 1){
            PlayerUtils.sendPlayerMessage(sender, CORRECT_USAGE);
            return;
        }

        // Target and Sender
        final String target = args[0];
        final String senderName = sender.getName();

        // Cancel self report
        if(target.equalsIgnoreCase(senderName)){
            PlayerUtils.sendPlayerMessage(sender, SELF_REPORT);
            return;
        }


        plugin.runAsync(() -> {

            boolean targetExists = plugin.getStorage().playerExists(target);

            if(!targetExists){
                PlayerUtils.sendPlayerMessage(sender, PLAYER_NOT_FOUND);
                return;
            }

            PlayerUtils.sendPlayerMessage(sender, SUCCESS_REPORT);
            plugin.createNewReport(senderName, target);


        });

    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return getStrings((ProxiedPlayer) sender, args, plugin);
    }

}

