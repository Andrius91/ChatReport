package icu.yogurt.common.interfaces;

import icu.yogurt.common.model.Message;

import java.util.List;
import java.util.Map;

public interface Storage {


    Map<String, String> getStaffMap();

    boolean playerExists(String player);

    /**
     *
     * Save the player message
     *
     * @param message message
     */
    void saveMessage(String player, Message message);

    /**
     *
     * Return the list messages from player
     *
     * @param player player
     * @return list of messages
     */
    List<Message> getMessages(String player);

    /**
     *
     * Returns a combined list of player1 and player2, sorted by date
     *
     * @param player1 player1
     * @param player2 player2
     * @param limit limit
     * @return combined list of player1 and player2
     */
    List<Message> getCombinedMessages(String player1, String player2, int limit);

    /**
     *
     * Get staff uuid from the username
     *
     * @param username username
     * @return uuid string
     */
    String getStaffUUID(String username);

    /**
     *
     * Update the staff uuid from the username
     *
     * @param username username
     * @param uuid uuid
     */
    void updateStaffUUID(String username, String uuid);
}
