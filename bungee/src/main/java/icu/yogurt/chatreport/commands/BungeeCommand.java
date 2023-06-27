package icu.yogurt.chatreport.commands;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.commands.BaseCommand;
import icu.yogurt.chatreport.impl.BungeePlayer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommand extends Command implements TabExecutor {

    private final BasePlugin plugin;
    private final BaseCommand baseCommand;
    public BungeeCommand(BasePlugin plugin, BaseCommand command) {
        super(command.getCrCommand().getCommand(),
                command.getCrCommand().getPermission(),
                command.getCrCommand().getAliases().toArray(new String[0]));
        this.baseCommand = command;
        this.plugin = plugin;
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
        BungeePlayer player = (BungeePlayer) plugin.getPlayerByUsername(sender.getName());
        baseCommand.execute(player, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        BungeePlayer player = (BungeePlayer) plugin.getPlayerByUsername(sender.getName());
        return baseCommand.onTabComplete(player, args);
    }
}
