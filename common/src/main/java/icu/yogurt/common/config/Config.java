package icu.yogurt.common.config;

import icu.yogurt.common.interfaces.IChatReport;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Config{

    private static final Map<String, YamlFile> playerConfigMap = new HashMap<>();

    private static final String PLAYERS_FOLDER = "players";

    public YamlFile get(IChatReport plugin, String fileName) {
        YamlFile yamlFile = null;
        File conf = new File(plugin.getDataFolder(), fileName);
        if (!conf.exists()) {
            try {
                InputStream in = Config.class.getResourceAsStream("/" + fileName);
                if(in != null){
                    Files.copy(in, conf.toPath());
                }else{
                    conf.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            yamlFile = new YamlFile(conf.getPath());
            yamlFile.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yamlFile;
    }

    public static void createFolder(IChatReport plugin) {
        File dataFolder = plugin.getDataFolder();
        if(!dataFolder.exists()){
            dataFolder.mkdir();
        }
        File folder = new File(dataFolder, PLAYERS_FOLDER);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    public static void reloadConfig(YamlFile configuration) {
        try {
            configuration.save();
            configuration.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static YamlFile createPlayerConfig(IChatReport plugin, String player) {
        YamlFile config = new Config().get(plugin,  PLAYERS_FOLDER + "/" + player + ".yml");
        config.set("uuid", "");

        playerConfigMap.put(player, config);
        return config;
    }

    public static YamlFile getPlayerConfig(String player){
        return playerConfigMap.get(player);
    }

    public static void reloadPlayerConfig(String player){
        YamlFile config = playerConfigMap.get(player);
        reloadConfig(config);
    }
}
