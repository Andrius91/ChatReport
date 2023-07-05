package icu.yogurt.chatreport.common.managers;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.cache.UserCache;
import icu.yogurt.chatreport.common.connector.RedisConnector;
import icu.yogurt.chatreport.common.interfaces.IStorage;
import icu.yogurt.chatreport.common.storage.RedisStorage;
import icu.yogurt.chatreport.common.storage.YamlStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static icu.yogurt.chatreport.common.ConfigKeys.STORAGE_TYPE;
import static icu.yogurt.chatreport.common.ConfigKeys.STORAGE_URL;

@RequiredArgsConstructor
public class StorageManager {

    private final BasePlugin plugin;
    @Getter
    private RedisConnector connector;
    @Getter
    private UserCache userCache;


    public IStorage initStorage(){
        // Storage
        String storage_type = STORAGE_TYPE.get();
        if(storage_type.equalsIgnoreCase("REDIS")){
            String url = STORAGE_URL.get();
            connector = new RedisConnector(url);
            this.userCache = new UserCache(plugin, connector);
            return new RedisStorage(plugin, connector);
        } else {
            return new YamlStorage();
        }
    }

    public void close(){
        connector.close();
    }
}
