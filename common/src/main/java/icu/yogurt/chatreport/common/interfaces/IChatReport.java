package icu.yogurt.chatreport.common.interfaces;

import com.google.gson.Gson;
import net.kyori.adventure.platform.AudienceProvider;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface IChatReport {
    Gson gson = new Gson();

    /**
     * Retrieves the audience provider that will be used for sending messages.
     * @return an instance of AudienceProvider.
     */
    AudienceProvider getAudienceProvider();

    /**
     * Retrieves the directory where the plugin data will be stored.
     * @return a File object representing the data directory.
     */
    File getDataFolder();

    /**
     * Logs a message with the given level.
     * @param level the level of the log message.
     * @param message the message to log.
     */
    void log(int level, String message);

    /**
     * Method to execute an asynchronous task using the scheduler.
     * @param runnable The asynchronous task to execute.
     */
    void runAsync(Runnable runnable);

    /**
     * Method to schedule a recurring task using the scheduler.
     * @param runnable The task to execute.
     * @param delay The delay before the first execution.
     * @param period The period between successive executions.
     * @param timeUnit The time unit of the delay and period parameters.
     */
    void scheduledTask(Runnable runnable, long delay, long period, TimeUnit timeUnit);

    /**
     * Executes a command.
     * @param command The command to execute.
     */
    void executeCommand(String command);

    /**
     * Retrieves a player by username.
     * @param username The username of the player.
     * @return An IPlayer object representing the player, or null if the player doesn't exist.
     */
    IPlayer getPlayerByUsername(String username);

    /**
     * Retrieves a list of players on a given server.
     * @param server The server to get the players list from.
     * @return A list of player usernames.
     */
    List<String> getPlayersList(String server);

    /**
     * Retrieves a list of players with a certain permission.
     * @param permission The permission to check for.
     * @return A list of IPlayer objects that have the given permission.
     */
    List<IPlayer> getPlayers(String permission);

    /**
     * Provides current UTC date and time as a string.
     * @return A string representing current date and time.
     */
    default String nowDate(){
        return LocalDateTime.now(ZoneOffset.UTC).toString();
    }
}
