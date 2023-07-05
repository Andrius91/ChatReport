package icu.yogurt.chatreport.common.commands;

import com.google.common.collect.ImmutableSet;
import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.managers.CooldownManager;
import icu.yogurt.chatreport.common.model.CRCommand;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static icu.yogurt.chatreport.common.ConfigKeys.*;

public abstract class BaseCommand {
    protected final CooldownManager cooldownManager;
    protected final BasePlugin plugin;
    @Getter
    protected final CRCommand crCommand;
    protected String senderName;
    protected String target;
    protected UUID playerUuid;

    protected abstract String getCommandName();
    protected abstract boolean argsValid(String[] args);
    protected abstract void executeAsync(IPlayer sender, String[] args);

    protected BaseCommand(BasePlugin plugin) {
        this.plugin = plugin;
        this.cooldownManager = new CooldownManager();
        this.crCommand = this.getCRCommand(getCommandName());
    }

    public void execute(IPlayer sender, String[] args) {
        if(argsValid(args)){
            sender.sendMessage(CORRECT_USAGE.replace("%command_usage%", crCommand.getUsage()));
            return;
        }

        this.playerUuid = sender.getUUID();


        // cooldown
        if(cooldownManager.hasCooldown(playerUuid) && !sender.hasPermission("pandoracrp.bypass.cooldown")){
            sender.sendMessage(HAS_COOLDOWN
                    .replace("%time%", cooldownManager.getTimeLeftStr(playerUuid)));
            return;
        }

        // Target and Sender
        target = args[0];

        if (target.length() > 16) {
            sender.sendMessage(CORRECT_USAGE);
            return;
        }

        senderName = sender.getUsername();

        // Cancel self report
        if(target.equalsIgnoreCase(senderName)){
            sender.sendMessage(SELF_REPORT);
            return;
        }

        plugin.runAsync(() ->{
            long startTime = System.currentTimeMillis();
            boolean targetExists = plugin.getStorage().playerExists(target);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            plugin.log(3, "(" + target + ":" +  targetExists + ") executed in "+ elapsedTime + "ms");

            if(!targetExists){
                sender.sendMessage(PLAYER_NOT_FOUND);
                return;
            }
            executeAsync(sender, args);
        });
    }

    public Iterable<String> onTabComplete(IPlayer sender, String[] args) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            String server = sender.getCurrentServerName();
            List<String> playerList = plugin.getPlayersList(server);

            for(String player : playerList){
                if(player.toLowerCase().startsWith(search)){
                    matches.add(player);
                }
            }
        }
        return matches;
    }

    private CRCommand getCRCommand(String command){
        String name = plugin.getConfig().getString("commands."+command+".command");
        String usage = plugin.getConfig().getString("commands."+command+".usage");
        String permission = plugin.getConfig().getString("commands."+command+".permission");
        List<String> aliases = plugin.getConfig().getStringList("commands."+command+".aliases");

        return new CRCommand(name, usage, permission, aliases);
    }

}
