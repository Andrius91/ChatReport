package icu.yogurt.chatreport.common.commands;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.model.Report;
import icu.yogurt.chatreport.common.model.ReportOption;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportCommand extends BaseCommand {

    private final String HEADER;
    private final String MAX_CHARS;

    public ReportCommand(BasePlugin plugin){
        super(plugin);
        HEADER = plugin.getConfig().getString("report.header");
        MAX_CHARS = plugin.getLangConfig().getString("lang.max-chars");
    }

    @Override
    protected String getCommandName(){
        return "report";
    }

    @Override
    protected boolean argsValid(String[] args){
        return args.length < 1;
    }

    @Override
    protected void executeAsync(IPlayer sender, String[] args){
        if(args.length == 1){
            sender.sendMessage(HEADER);
            sender.sendMessage(reportOptions(target));
            return;
        }

        StringBuilder reason = new StringBuilder();
        Report report = new Report();
        String server = sender.getCurrentServerName();

        report.setTarget(target);
        report.setSender(senderName);
        report.setServer(server);
        // Get all strings
        for (int i = 1; i < args.length; i++){
            if(reason.length() > 36){
                sender.sendMessage(MAX_CHARS);
                return;
            }
            reason.append(args[i]).append(" ");
        }

        String reasonStr = reason.toString().trim();

        report.setReason(reasonStr);

        sender.sendMessage(SUCCESS_REPORT);

        // Cooldown
        cooldownManager.addPlayer(playerUuid);

        plugin.sendReportToStaffs(report, true);

    }

    private List<String> reportOptions(String target){
        YamlFile config = plugin.getConfig();
        List<String> options = new ArrayList<>();
        List<?> list = config.getList("report.options");

        List<ReportOption> optionsList = list.stream()
                .filter(object -> object instanceof ReportOption)
                .map(object -> (ReportOption) object)
                .collect(Collectors.toList());

        for (ReportOption option : optionsList){
            StringBuilder sb = new StringBuilder();
            String text = option.getText();
            String command = option.getCommand().replace("%target%", target);
            String hover = option.getHover().replace("%target%", target);

            sb.append("text: ");
            sb.append(text);
            sb.append(" hover:");
            sb.append(hover);
            sb.append(" action:RUN_COMMAND:");
            sb.append(command);

            String message = sb.toString();

            options.add(message);
        }

        return options;
    }

}
