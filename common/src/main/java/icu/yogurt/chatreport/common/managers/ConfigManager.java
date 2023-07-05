package icu.yogurt.chatreport.common.managers;

import icu.yogurt.chatreport.common.BasePlugin;
import icu.yogurt.chatreport.common.config.Config;
import icu.yogurt.chatreport.common.model.Message;
import icu.yogurt.chatreport.common.model.ReportOption;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.serialization.ConfigurationSerialization;

@Getter
public class ConfigManager {
    private YamlFile config;
    private YamlFile langConfig;
    private YamlFile punishmentConfig;

    public ConfigManager(BasePlugin plugin) {
        // Load config serializers/deserialziers
        loadConfigsSerializers();

        // Load configs
        loadConfigs(plugin);
    }

    private void loadConfigs(BasePlugin plugin) {
        this.config = new Config().get(plugin, "config.yml");
        this.langConfig = new Config().get(plugin, "lang.yml");
        this.punishmentConfig = new Config().get(plugin, "punishment.yml");
    }

    private void loadConfigsSerializers(){
        ConfigurationSerialization.registerClass(Message.class);
        ConfigurationSerialization.registerClass(ReportOption.class);
    }

}
