package icu.yogurt.chatreport.common.config;

import icu.yogurt.chatreport.common.BasePlugin;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.api.QuoteStyle;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Map<String, YamlFile> playerConfigMap = new HashMap<>();

    private static final String PLAYERS_FOLDER = "players";


    @SneakyThrows
    public YamlFile get(BasePlugin plugin, String fileName){
        YamlFile yamlFile = new YamlFile(new File(plugin.getDataFolder(), fileName));

        if(!yamlFile.exists()){
            InputStream in = getClass().getResourceAsStream("/" + fileName);
            if(in != null){
                Files.copy(in, Paths.get(yamlFile.getFilePath()));
            } else {
                yamlFile.createNewFile();
            }
        }
        yamlFile.load();
        yamlFile.options().quoteStyleDefaults().setQuoteStyle(String.class, QuoteStyle.DOUBLE);
        yamlFile.options().quoteStyleDefaults().setQuoteStyle(List.class, QuoteStyle.DOUBLE);

        return yamlFile;
    }


    public static void reloadConfig(YamlFile configuration){
        try{
            configuration.save();
            configuration.load();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static YamlFile createPlayerConfig(BasePlugin plugin, String player){
        YamlFile config = new Config().get(plugin, PLAYERS_FOLDER + File.separator + player + ".yml");
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
