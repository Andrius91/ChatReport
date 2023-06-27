package icu.yogurt.chatreport.common.interfaces;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import icu.yogurt.chatreport.common.API;
import icu.yogurt.chatreport.common.connector.DatabaseConnector;
import icu.yogurt.chatreport.common.cache.UserCache;
import icu.yogurt.chatreport.common.model.ChatReport;
import icu.yogurt.chatreport.common.model.Message;
import icu.yogurt.chatreport.common.service.PunishmentService;
import net.kyori.adventure.platform.AudienceProvider;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface IChatReport {
    Gson gson = new Gson();

    AudienceProvider getAudienceProvider();
    RedisBungeeAPI getRedisBungeeAPI();
    boolean isDebug();
    void setDebug(boolean value);
    boolean isRedisBungeeAvailable();
    void log(int level, String message);

    PunishmentService getPunishmentService();

    UserCache getUserCache();
    File getDataFolder();

    IStorage getStorage();
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
    void scheduledTask(Runnable runnable, long delay, long period, TimeUnit timeUnit);

    void executeCommand(String command);

    IPlayer getPlayerByUsername(String username);
    List<String> getPlayersList(String server);
    List<IPlayer> getPlayers(String permission);
    default void createNewReport(String sender, String target){
        runAsync(() -> {
            List<Message> combinedMessages = getStorage().getCombinedMessages(sender, target, 25);
            ChatReport chatReport = new ChatReport(null, sender, target, combinedMessages);
            String json = gson.toJson(chatReport);

            CompletableFuture<String> response = getApi().postAsync("/api/chatreports", json);
            response.thenAcceptAsync(result -> {
                ChatReport createdChatReport = gson.fromJson(result, ChatReport.class);
                log(3, "New report created: " + createdChatReport);
            });
        });
    }


    default String nowDate(){
        return LocalDateTime.now(ZoneOffset.UTC).toString();
    }


}
