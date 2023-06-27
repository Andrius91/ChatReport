package icu.yogurt.chatreport.common;

import com.saicone.ezlib.Dependencies;
import com.saicone.ezlib.Dependency;
import com.saicone.ezlib.EzlibLoader;

import java.io.File;

@Dependencies(
        value = {
                // Jedis
                @Dependency(value = "redis.clients:jedis:4.3.0"),
                @Dependency(value = "org.slf4j:slf4j-nop:1.7.36"), // For slf4j-api
                // Simple YAML
                @Dependency(value = "me.carleslc.Simple-YAML:Simple-Yaml:1.8.4",
                        relocate = {"org.simpleyaml", "{package}.libs.yaml"}),
                // OkHttp3
                @Dependency(value = "com.squareup.okhttp3:okhttp:4.2.2", relocate = {
                        "com.squareup", "{package}.libs.okhttp3"
                }),
                // Adventure
                @Dependency(value = "net.kyori:adventure-api:4.14.0", relocate = {
                        "net.kyori", "{package}.libs.net.kyori"
                }),
                // Adventure text serializer
                @Dependency(value = "net.kyori:adventure-text-serializer-legacy:4.14.0", relocate = {
                        "net.kyori", "{package}.libs.net.kyori"
                }),
                // Adventure bungee platform
                @Dependency(value = "net.kyori:adventure-platform-bungeecord:4.3.0", relocate = {
                        "net.kyori", "{package}.libs.net.kyori"
                }),
        },
        relocations = {
                // Jedis
                "redis.clients.jedis", "{package}.libs.jedis",
                "com.google.gson", "{package}.libs.gson",
                "org.apache.commons.pool2", "{package}.libs.commons.pool2",
                "org.json", "{package}.libs.json",
                "org.slf4j", "{package}.libs.slf4j"
        }
)
public class LibraryLoader {

    public static void loadLibraries(File folder){
        EzlibLoader loader = new EzlibLoader(new File(folder, "libs"));
        loader.replace("{package}", "icu.yogurt");
        loader.load();
    }
}
