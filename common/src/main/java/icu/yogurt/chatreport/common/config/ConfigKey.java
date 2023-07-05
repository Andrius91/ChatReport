package icu.yogurt.chatreport.common.config;

import org.simpleyaml.configuration.file.YamlFile;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConfigKey<T> {
    private final Supplier<YamlFile> configSupplier;
    private final String key;
    private final Replacer replacer;

    public ConfigKey(Supplier<YamlFile> configSupplier, String key) {
        this(configSupplier, key, null);
    }

    private ConfigKey(Supplier<YamlFile> configSupplier, String key, Replacer replacer) {
        this.configSupplier = configSupplier;
        this.key = key;
        this.replacer = replacer;
    }

    public ConfigKey<T> replace(Replacer replacer) {
        return new ConfigKey<>(configSupplier, key, replacer);
    }

    public ConfigKey<T> replace(String replace, String replacement) {
        Replacer replacer = new Replacer(replace, replacement);
        return replace(replacer);
    }

    public String get() {
        String value = configSupplier.get().getString(key);
        if (replacer != null) {
            value = replacer.apply(value);
        }
        return value;
    }

    public Integer getAsInteger(){
        return configSupplier.get().getInt(key);
    }
    public boolean getAsBoolean() {
        return configSupplier.get().getBoolean(key);
    }

    public List<String> getAsStringList() {
        List<String> valueList = configSupplier.get().getStringList(key);
        if (replacer != null) {
            valueList = valueList.stream().map(replacer::apply).collect(Collectors.toList());
        }
        return valueList;
    }

}
