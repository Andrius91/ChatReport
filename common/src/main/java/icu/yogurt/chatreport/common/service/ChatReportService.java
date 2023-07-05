package icu.yogurt.chatreport.common.service;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.model.ChatReport;
import icu.yogurt.chatreport.common.model.Message;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ChatReportService {

    private final BasePlugin plugin;

    public void createNewReport(String sender, String target){
        plugin.runAsync(() -> {
            List<Message> combinedMessages = plugin.getStorage().getCombinedMessages(sender, target, 25);
            ChatReport chatReport = new ChatReport(null, sender, target, combinedMessages);
            String json = plugin.gson.toJson(chatReport);

            CompletableFuture<String> response = plugin.getApi().postAsync("/api/chatreports", json);
            response.thenAcceptAsync(result -> {
                ChatReport createdChatReport = plugin.gson.fromJson(result, ChatReport.class);
                plugin.log(3, "New report created: " + createdChatReport);
            });
        });
    }
}
