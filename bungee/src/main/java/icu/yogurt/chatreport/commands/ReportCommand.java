package icu.yogurt.chatreport.commands;

import com.google.common.collect.ImmutableSet;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.utils.PlayerUtils;
import icu.yogurt.common.model.CRCommand;
import icu.yogurt.common.model.Report;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportCommand extends Command implements TabExecutor {
    private final ChatReport plugin;
    private final String PLAYER_NOT_FOUND;
    private final String SUCCESS_REPORT;
    private final String CORRECT_USAGE;
    private final String HEADER;
    private final String SELF_REPORT;
    private final String MAX_CHARS;

    public ReportCommand(ChatReport plugin, CRCommand command) {
        super(command.getCommand(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.plugin = plugin;
        PLAYER_NOT_FOUND = plugin.getLangConfig().getString("lang.player-does-not-exist");
        SUCCESS_REPORT = plugin.getLangConfig().getString("lang.success-report");
        CORRECT_USAGE = plugin.getLangConfig().getString("lang.correct-usage")
                .replace("%command_usage%", command.getUsage());
        HEADER = plugin.getConfig().getString("report.header");
        SELF_REPORT = plugin.getLangConfig().getString("lang.self-report");
        MAX_CHARS = plugin.getLangConfig().getString("lang.max-chars");

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            plugin.log(4, "This command can only be executed by players");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if(args.length < 1){
            PlayerUtils.sendPlayerMessage(sender, CORRECT_USAGE);
            return;
        }

        // Target and Sender
        final String target = args[0];
        final String senderName = sender.getName();
        final String server = player.getServer().getInfo().getName();
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

            if(args.length == 1){
                PlayerUtils.sendPlayerMessage(sender, HEADER);
                for(TextComponent component : PlayerUtils.reportOptions(plugin, target)){
                    player.sendMessage(component);
                }
                return;
            }

            StringBuilder reason = new StringBuilder();
            Report report = new Report();

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

            if(plugin.isRedisBungee()){
                String json = plugin.gson.toJson(report);
                RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage("pandora:report", json);
            }else{
                PlayerUtils.sendReportToStaffs(plugin, report);
            }
        });

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return getStrings((ProxiedPlayer) sender, args, plugin);
    }

    @NotNull
    static Iterable<String> getStrings(ProxiedPlayer sender, String[] args, ChatReport plugin) {
        if (args.length > 2 || args.length == 0) {
            return ImmutableSet.of();
        }

        Set<String> matches = new HashSet<>();
        if (args.length == 1) {
            String search = args[0].toLowerCase();
            ProxiedPlayer p = sender;
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
