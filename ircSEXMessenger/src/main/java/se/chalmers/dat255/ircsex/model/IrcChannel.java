package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an IrcChannel and handles messages sent in it.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcChannel {

    private final String channelName;
    private Map<String, IrcUser> users;
    private final List<IrcMessage> messages;

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName Name of channel
     */
    public IrcChannel(String channelName) {
        this.channelName = channelName;
        this.users = new HashMap<String, IrcUser>();
        messages = new ArrayList<IrcMessage>();
    }

    /**
     * Returns the name of the channel.
     *
     * @return Name of the channel
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Empties and sets the list of users.
     *
     * @param users - A list with the users
     */
    public void addUsers(List<String> users) {
        for (String user : users) {
            char status = IrcUser.extractUserStatus(user);
            user = IrcUser.extractUserName(user);
            this.users.put(user, new IrcUser(user, status));
        }
    }

    /**
     * Adds a user to the list of users.
     *
     * @param user - The user who joined
     */
    public void userJoined(String user) {
        char status = IrcUser.extractUserStatus(user);
        user = IrcUser.extractUserName(user);
        users.put(user, new IrcUser(user, status));
    }

    /**
     * A user changed nickname.
     *
     * @param oldNick Old nickname
     * @param newNick New nickname
     */
    public void nickChanged(String oldNick, String newNick) {
        oldNick = IrcUser.extractUserName(oldNick);
        if (users.containsKey(oldNick)) {
            IrcUser user = users.get(oldNick);
            user.changeNick(newNick);
            users.remove(oldNick);
            users.put(newNick, user);
        }
    }

    /**
     * Removes a user from the list of users.
     *
     * @param user - The user who left
     */
    public void userParted(String user) {
        user = IrcUser.extractUserName(user);
        users.remove(user);
    }

    /**
     * Returns a list with the names of all users.
     *
     * @return - A list with all users
     */
    public List<IrcUser> getUsers() {
        List<IrcUser> users = new ArrayList<IrcUser>(this.users.values());
        Collections.sort(users);
        return users;
    }

    /**
     * Return all messages.
     *
     * @return The messages in this channel
     */
    public List<IrcMessage> getMessages() {
        return messages;
    }

    /**
     * Adds a message to undread.
     *
     * @param user User who sent the message
     * @param message Message to add
     * @param timestamp Time when message was sent
     */
    public void newMessage(String user, String message, long timestamp) {
        user = IrcUser.extractUserName(user);
        messages.add(new IrcMessage(user, message, timestamp));
    }

    /**
     * Marks message as read.
     *
     * @param message Message that will be marked as read
     */
    public void readMessage(IrcMessage message) {
        message.read();
    }
}
