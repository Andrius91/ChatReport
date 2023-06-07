package icu.yogurt.common.interfaces;

import com.google.gson.Gson;
import icu.yogurt.common.API;
import icu.yogurt.common.cache.UserCache;
import icu.yogurt.common.connector.DatabaseConnector;
import icu.yogurt.common.model.ChatReport;
import icu.yogurt.common.model.Message;
import icu.yogurt.common.service.PunishmentService;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatReport {
    Gson gson = new Gson();

    void log(int level, String message);

    PunishmentService getPunishmentService();

    UserCache getUserCache();
    File getDataFolder();

    Storage getStorage();
    DatabaseConnector getDatabase();

    API getApi();

    YamlFile getConfig();

    YamlFile getPunishmentConfig();
    YamlFile getLangConfig();

    /**
     * Method to execute an asynchronous task using the scheduler.
     * @param runnable The asynchronous task to execute.
     */
    void runAsync(Runnable runnable);

    void executeCommand(String command);

    List<String> getPlayersList(String server);
    default void createNewReport(String sender, String target){
        runAsync(() -> {
            List<Message> combinedMessages = getStorage().getCombinedMessages(sender, target, 25);
            ChatReport chatReport = new ChatReport(null, sender, target, combinedMessages);
            String json = gson.toJson(chatReport);

            CompletableFuture<String> response = getApi().postAsync("/api/chatreports", json);
            response.thenAcceptAsync(result -> {
                ChatReport createdChatReport = gson.fromJson(result, ChatReport.class);
                log(3, "New reporte created: " + createdChatReport);
            });
        });
    }

    default String nowDate(){
        return LocalDateTime.now(ZoneOffset.UTC).toString();
    }


}
