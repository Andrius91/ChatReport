package icu.yogurt.chatreport.utils;

import icu.yogurt.chatreport.ChatReport;
import icu.yogurt.chatreport.common.model.Report;
import icu.yogurt.chatreport.common.model.ReportOption;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerUtils {

    public static BaseComponent[] color(String txt) {
        if (txt == null) {
            throw new NullPointerException("String empty");
        }
        return new ComponentBuilder(textColor(txt)).create();
    }

    public static void sendPlayerMessage(CommandSender p, String message){
        p.sendMessage(color(message));
    }
    public static void sendPlayerMessage(String sender, String message){
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(sender);
        p.sendMessage(color(message));
    }

    public static String textColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<TextComponent> reportOptions(ChatReport plugin, String target){
        List<TextComponent> textComponents = new ArrayList<>();
        YamlFile config = plugin.getConfig();
        List<Object> optionsList = (List<Object>) config.getList("report.options");

        List<ReportOption> reportOptionList = optionsList.stream()
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .map(map -> new ReportOption((String) map.get("text"),
                        (String) map.get("hover"),
                        (String) map.get("command")
                ))
                .collect(Collectors.toList());

        for(ReportOption option : reportOptionList){
            TextComponent component = new TextComponent();
            component.setText(textColor(option.getText()));

            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    option.getCommand()
                            .replace("%target%", target)));

            Text text = new Text(color(option.getHover()
                    .replace("%target%", target)));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                   text));

            textComponents.add(component);
        }

        return textComponents;
    }

    public static void sendReportToStaffs(ChatReport plugin, Report report){
        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
            if(player.hasPermission("pandoracrp.staff.notify")){
                List<String> message = plugin.getConfig().getStringList("report.messages.sent-to.staffs").stream().map(
                        x -> x.replace("%reporter%", report.getSender())
                                .replace("%target%", report.getTarget())
                                .replace("%reason%", report.getReason())
                                .replace("%server%", report.getServer())
                ).collect(Collectors.toList());

                message.forEach( m -> sendPlayerMessage(player, m));

                player.sendMessage(getServerComponent(plugin, report.getServer()));
            }
        }
    }

    private static TextComponent getServerComponent(ChatReport plugin, String server){
        String text = plugin.getConfig().getString("report.messages.server-component.text");
        String hover_text = plugin.getConfig().getString("report.messages.server-component.hover");
        String command = plugin.getConfig().getString("report.messages.server-component.command")
                .replace("%server%", server);


        TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));

        Text content = new Text(color(hover_text));

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));

        return message;
    }
}
