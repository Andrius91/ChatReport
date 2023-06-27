package icu.yogurt.chatreport.common.model;

import lombok.*;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Message")
public class Message implements ConfigurationSerializable {

    private String message;
    private String server;
    private String sender;
    private String date;

    @SuppressWarnings("unused")
    public Message(Map<String, Object> mappedObject) {
        this.message = (String) mappedObject.get("message");
        this.server = (String) mappedObject.get("server");
        this.sender = (String) mappedObject.get("sender");
        this.date = (String) mappedObject.get("date");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> mappedObject = new LinkedHashMap<>();
        mappedObject.put("message", this.message);
        mappedObject.put("server", this.server);
        mappedObject.put("sender", this.sender);
        mappedObject.put("date", this.date);
        return mappedObject;
    }

    public LocalDateTime getLocalDateTime(){
        return LocalDateTime.parse(date);
    }

}
