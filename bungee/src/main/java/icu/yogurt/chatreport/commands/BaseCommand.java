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

public abstract class BaseCommand extends Command implements TabExecutor {
    protected final ChatReport plugin;
    protected final String PLAYER_NOT_FOUND;
    protected final String SUCCESS_REPORT;
    protected final String CORRECT_USAGE;
    protected final String SELF_REPORT;
    protected String senderName, target;

    protected abstract boolean argsValid(String[] args);
    protected abstract void executeAsync(CommandSender sender, String[] args);

    public BaseCommand(ChatReport plugin, CRCommand command) {
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

        if(argsValid(args)){
            PlayerUtils.sendPlayerMessage(sender, CORRECT_USAGE);
            return;
        }

        // Target and Sender
        target = args[0];
        senderName = sender.getName();

        // Cancel self report
        if(target.equalsIgnoreCase(senderName)){
            PlayerUtils.sendPlayerMessage(sender, SELF_REPORT);
            return;
        }

        plugin.runAsync(() ->{
            boolean targetExists = plugin.getStorage().playerExists(target);

            if(!targetExists){
                PlayerUtils.sendPlayerMessage(sender, PLAYER_NOT_FOUND);
                return;
            }

            executeAsync(sender, args);
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
