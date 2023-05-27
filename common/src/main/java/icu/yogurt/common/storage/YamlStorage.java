package icu.yogurt.common.storage;

import icu.yogurt.common.config.Config;
import icu.yogurt.common.interfaces.Storage;
import icu.yogurt.common.model.Message;
import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class YamlStorage implements Storage {
    private final Map<String, String> staffs_map = new HashMap<>();

    @Override
    public Map<String, String> getStaffMap() {
        return staffs_map;
    }

    @Override
    public boolean playerExists(String player) {
        return Config.getPlayerConfig(player) != null;
    }

    @Override
    public void saveMessage(Message message) {

        String player = message.getSender();
        YamlFile config = Config.getPlayerConfig(player);

        List<Map<String, Object>> messages = getMessages(player)
                .stream()
                .map(this::messageToMap)
                .collect(Collectors.toList());

        messages.add(messageToMap(message));

        if (messages.size() > 25) {
            messages.remove(0); // remove the oldest message
        }

        config.set("messages", messages);

        Config.reloadPlayerConfig(player);
    }


    private Map<String, Object> messageToMap(Message message) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("message", message.getMessage());
        map.put("server", message.getServer());
        map.put("sender", message.getSender());
        map.put("date", message.getDate());
        return map;
    }

    @Override
    public List<Message> getMessages(String playerName) {
        YamlFile config = Config.getPlayerConfig(playerName);

        @SuppressWarnings("unchecked")
        List<Object> messageList = (List<Object>) config.getList("messages");

        return messageList.stream()
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .map(map -> new Message((String) map.get("message"),
                        (String) map.get("server"),
                        (String) map.get("sender"),
                        (String) map.get("date")
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getCombinedMessages(String player1, String player2, int limit) {
        List<Message> player1Messages = getMessages(player1);
        List<Message> player2Messages = getMessages(player2);

        return Stream.concat(player1Messages.stream(), player2Messages.stream())
                .sorted(Comparator.comparing(Message::getLocalDateTime))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getUserUUID(String username) {
        return null;
    }

    @Override
    public void saveMessages(List<Message> messages) {

    }

}
