package icu.yogurt.chatreport.common;

import icu.yogurt.chatreport.common.config.ConfigKey;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.List;
import java.util.function.Supplier;

import static icu.yogurt.chatreport.common.BasePlugin.getPlugin;

public class ConfigKeys {

    private static final Supplier<YamlFile> LANG_CONFIG_SUPPLIER = getPlugin()::getLangConfig;
    private static final Supplier<YamlFile> MAIN_CONFIG_SUPPLIER = getPlugin()::getConfig;
    private static final Supplier<YamlFile> PUNISHMENT_CONFIG_SUPPLIER = getPlugin()::getPunishmentConfig;

    // Main configs
    public static final ConfigKey<Boolean> DEBUG = configKey(MAIN_CONFIG_SUPPLIER, "debug");
    public static final ConfigKey<Boolean> USE_REDIS_BUNGEE = configKey(MAIN_CONFIG_SUPPLIER, "use-redis-bungee");

    public static final ConfigKey<Boolean> SAVE_MESSAGES = configKey(MAIN_CONFIG_SUPPLIER, "messages.save-messages");
    public static final ConfigKey<Boolean> SAVE_COMMANDS = configKey(MAIN_CONFIG_SUPPLIER, "messages.save-commands");
    public static final ConfigKey<List<String>> COMMANDS_LIST = configKey(MAIN_CONFIG_SUPPLIER, "messages.commands");
    public static final ConfigKey<String> API_HOST = configKey(MAIN_CONFIG_SUPPLIER, "api.host");
    public static final ConfigKey<String> API_KEY = configKey(MAIN_CONFIG_SUPPLIER, "api.key");
    //public static final ConfigKey<String> DATABASE_TYPE = configKey(MAIN_CONFIG_SUPPLIER, "database.type");
    public static final ConfigKey<String> DATABASE_USER = configKey(MAIN_CONFIG_SUPPLIER, "database.user");
    public static final ConfigKey<String> DATABASE_PASSWORD = configKey(MAIN_CONFIG_SUPPLIER, "database.password");
    public static final ConfigKey<String> DATABASE_URL = configKey(MAIN_CONFIG_SUPPLIER, "database.url");
    public static final ConfigKey<String> STORAGE_TYPE = configKey(MAIN_CONFIG_SUPPLIER, "storage.type");
    public static final ConfigKey<String> STORAGE_URL = configKey(MAIN_CONFIG_SUPPLIER, "storage.url");

    public static final ConfigKey<Integer> REPORT_COOLDOWN = configKey(MAIN_CONFIG_SUPPLIER, "report.cooldown");
    public static final ConfigKey<Integer> REPORT_MAX_REASON_CHARS = configKey(MAIN_CONFIG_SUPPLIER,
            "report.max-reason-chars");
    public static final ConfigKey<List<String>> MESSAGES_SENT_TO_STAFFS = configKey(MAIN_CONFIG_SUPPLIER,
            "report.messages.sent-to.staffs");

    public static final ConfigKey<String> REPORT_OPTION_HEADER = configKey(MAIN_CONFIG_SUPPLIER,
            "report.header");

    // Lang configs
    public static final ConfigKey<String> PLAYER_NOT_FOUND = configKey(LANG_CONFIG_SUPPLIER,
            "lang.player-not-found");
    public static final ConfigKey<String> CORRECT_USAGE = configKey(LANG_CONFIG_SUPPLIER,
            "lang.correct-usage");
    public static final ConfigKey<String> MAX_CHARS = configKey(LANG_CONFIG_SUPPLIER,
            "lang.max-chars");
    public static final ConfigKey<String> SELF_REPORT = configKey(LANG_CONFIG_SUPPLIER,
            "lang.self-report");
    public static final ConfigKey<String> SUCCESS_REPORT = configKey(LANG_CONFIG_SUPPLIER,
            "lang.success-report");
    public static final ConfigKey<String> HAS_COOLDOWN = configKey(LANG_CONFIG_SUPPLIER,
            "lang.has-cooldown");
    public static final ConfigKey<String> NO_MESSAGES_FOUND = configKey(LANG_CONFIG_SUPPLIER,
            "lang.no-messages-found");

    // Punishment configs
    public static final ConfigKey<Boolean> AUTO_TASK = configKey(PUNISHMENT_CONFIG_SUPPLIER,
            "punishment.auto-task");
    public static final ConfigKey<Boolean> AUTO_ON_JOIN = configKey(PUNISHMENT_CONFIG_SUPPLIER,
            "punishment.auto-on-join");
    public static final ConfigKey<List<String>> TYPES_ON_JOIN = configKey(PUNISHMENT_CONFIG_SUPPLIER,
            "punishment.types.on-join");
    public static final ConfigKey<List<String>> TYPES_TASK = configKey(PUNISHMENT_CONFIG_SUPPLIER,
            "punishment.types.task");

    // A helper method to create a ConfigKey with less repetition.
    private static <T> ConfigKey<T> configKey(Supplier<YamlFile> configSupplier, String path) {
        return new ConfigKey<>(configSupplier, path);
    }
}
