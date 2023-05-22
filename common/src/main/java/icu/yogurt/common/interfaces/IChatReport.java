package icu.yogurt.common.interfaces;

import com.google.gson.Gson;
import icu.yogurt.common.API;
import icu.yogurt.common.model.Message;
import icu.yogurt.common.model.Report;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatReport {
    Gson gson = new Gson();

    void log(int level, String message);
    File getDataFolder();

    Storage getStorage();

    API getApi();

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
            Report report = new Report(null, sender, target, combinedMessages);
            String json = gson.toJson(report);

            System.out.println("json = " + json);
            CompletableFuture<String> response = getApi().postAsync("/api/chatreports", json);
            response.thenAcceptAsync(result -> {
                Report createdReport = gson.fromJson(result, Report.class);
                System.out.println("New reporte created: " + createdReport);
            });
        });


    }


}
