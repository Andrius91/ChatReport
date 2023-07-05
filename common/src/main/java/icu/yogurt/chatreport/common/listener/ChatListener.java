package icu.yogurt.chatreport.common.listener;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.interfaces.IPlayer;
import icu.yogurt.chatreport.common.model.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static icu.yogurt.chatreport.common.ConfigKeys.COMMANDS_LIST;
import static icu.yogurt.chatreport.common.ConfigKeys.SAVE_COMMANDS;

public abstract class ChatListener{
    private final BasePlugin plugin;

    private final ExecutorService executorService;

    public ChatListener(BasePlugin plugin){
        this.plugin = plugin;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void process(IPlayer player, String playerMessage, boolean isCommand){
        String playerName = player.getUsername();
        String server = player.getCurrentServerName();

        if (isCommand && SAVE_COMMANDS.getAsBoolean()) {
            String command = playerMessage.split(" ")[0].replace("/", "").trim();
            if (!COMMANDS_LIST.getAsStringList().contains(command)) {
                return;
            }
        }

        executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            Message message = new Message(playerMessage, server, playerName, plugin.nowDate());
            plugin.getStorage().saveMessage(message);
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            plugin.log(3, "(" + playerName + ":" + playerMessage + ") saved in " + elapsedTime + "ms");
        });
    }
}
