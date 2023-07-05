package icu.yogurt.chatreport.common.storage;

import icu.yogurt.chatreport.common.config.Config;
import icu.yogurt.chatreport.common.interfaces.IStorage;
import icu.yogurt.chatreport.common.model.Message;
import lombok.RequiredArgsConstructor;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.api.QuoteStyle;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class YamlStorage implements IStorage {

    @Override
    public boolean playerExists(String player) {
        return Config.getPlayerConfig(player) != null;
    }

    @Override
    public boolean playerHasMessages(String player) {
        List<Message> playerMessages = getMessages(player);
        return playerMessages.size() > 0;
    }

    @Override
    public void saveMessage(Message message) {

        String player = message.getSender();
        YamlFile config = Config.getPlayerConfig(player);

        List<Message> messages = getMessages(player);

        messages.add(message);

        if (messages.size() > 25) {
            messages.remove(0); // remove the oldest message
        }

        config.set("messages", messages, QuoteStyle.PLAIN);

        Config.reloadPlayerConfig(player);
    }



    @Override
    public List<Message> getMessages(String playerName) {
        YamlFile config = Config.getPlayerConfig(playerName);
        List<?> messageList = config.getList("messages");
        return messageList.stream()
                .filter(object -> object instanceof Message)
                .map(object -> (Message) object)
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
        YamlFile config = Config.getPlayerConfig(username);
        return config != null ? config.getString("uuid") : "0";
    }

    @Override
    public void saveMessages(List<Message> messages) {

    }

}
