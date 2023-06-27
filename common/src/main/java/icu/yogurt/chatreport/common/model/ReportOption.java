package icu.yogurt.chatreport.common.model;

import lombok.*;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SerializableAs("ReportOption")
public class ReportOption implements ConfigurationSerializable {

    private String text;
    private String hover;
    private String command;

    @SuppressWarnings("unused")
    public ReportOption(Map<String, Object> mappedObject) {
        this.text = (String) mappedObject.get("text");
        this.hover = (String) mappedObject.get("hover");
        this.command = (String) mappedObject.get("command");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> mappedObject = new LinkedHashMap<>();
        mappedObject.put("text", this.text);
        mappedObject.put("hover", this.hover);
        mappedObject.put("command", this.command);
        return mappedObject;
    }
}
