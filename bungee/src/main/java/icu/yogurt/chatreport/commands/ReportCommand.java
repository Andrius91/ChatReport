package icu.yogurt.chatreport.commands;

import com.google.common.collect.ImmutableSet;
import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.common.model.CRCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportCommand extends Command implements TabExecutor {
    private final ChatReport plugin;
    private final String PLAYER_NOT_FOUND;
    private final String SUCCESS_REPORT;
    private final String CORRECT_USAGE;

    public ReportCommand(ChatReport plugin, CRCommand command) {
        super(command.getCommand(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.plugin = plugin;
        PLAYER_NOT_FOUND = plugin.getConfig().getString("lang.player-does-not-exist");
        SUCCESS_REPORT = plugin.getConfig().getString("lang.success-report");
        CORRECT_USAGE = plugin.getConfig().getString("lang.correct-usage");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            plugin.log(1, "Command only available for players");
            return;
        }

        if(args.length != 1){
            PlayerUtils.sendPlayerMessage(sender, CORRECT_USAGE);
            return;
        }

        // Target and Sender
        final String target = args[0];
        final String senderName = sender.getName();

        plugin.runAsync(() -> {

            boolean playerExist = plugin.getStorage().playerExists(target);

            if(playerExist){
                PlayerUtils.sendPlayerMessage(sender, SUCCESS_REPORT);
                plugin.createNewReport(senderName, target);
            }else{
                PlayerUtils.sendPlayerMessage(sender, PLAYER_NOT_FOUND);
            }

        });

    }


    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            ProxiedPlayer p = (ProxiedPlayer) sender;
            String server = p.getServer().getInfo().getName();
            List<String> playerList = plugin.getPlayersList(server);

            for(String player : playerList){
                if(player.toLowerCase().startsWith(search)){
                    matches.add(player);
                }
            }
        }
        return matches;
    }
}

