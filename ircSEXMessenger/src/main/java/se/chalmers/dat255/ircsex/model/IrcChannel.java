package se.chalmers.dat255.ircsex.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class represents an IrcChannel and handles messages sent in it.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcChannel {

    private final String channelName;
    private ConcurrentMap<String, IrcUser> users;
    private final List<IrcMessage> messages;

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName Name of channel
     */
    public IrcChannel(String channelName) {
        this.channelName = channelName;
        this.users = new ConcurrentHashMap<String, IrcUser>();
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
        synchronized (users) {
            for (String user : users) {
                char status = IrcUser.extractUserStatus(user);
                user = IrcUser.extractUserName(user);
                this.users.put(user, new IrcUser(user, status));
            }
        }
    }

    /**
     * Adds a user to the list of users.
     *
     * @param user - The user who joined
     */
    public void userJoined(IrcUser user) {
        synchronized (users) {
            users.put(user.getNick(), user);
        }
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
            messages.add(new InfoIrcMessage(oldNick + " is now known as " + newNick));
        }
    }

    /**
     * Removes a user from the list of users.
     *
     * @param user - The user who left
     */
    public void userParted(String user) {
        Log.d("IRC", user + " quit");
        synchronized (users) {
            Log.d("IRC", user + " quit");
            user = IrcUser.extractUserName(user);
            users.remove(user);
        }
    }

    /**
     * Returns a list with the names of all users.
     *
     * @return - A list with all users
     */
    public List<IrcUser> getUsers() {
        synchronized (users) {
            List<IrcUser> users = new ArrayList<IrcUser>(this.users.values());
            Collections.sort(users);
            return users;
        }
    }

    public IrcUser getUser(String nick) {
        return users.get(nick);
    }

    /**
     * Returns true if the user is in the channel.
     * @param user - the user to check
     */
    public boolean hasUser(String user) {
        return users.containsKey(user);
    }

    /**
     * Return all messages.
     *
     * @return The messages in this channel
     */
    public List<IrcMessage> getMessages() {
        synchronized (messages) {
            return messages;
        }
    }

    /**
     * Adds a message to unread.
     *
     * @param user User who sent the message
     * @param message Message to add
     * @return The ChatIrcMessage created from the message string and user string
     */
    public ChatIrcMessage newChatMessage(String user, String message) {
        return newChatMessage(user, message, false);
    }

    public ChatIrcMessage newChatMessage(String user, String message, boolean highlight) {
        synchronized (messages) {
            user = IrcUser.extractUserName(user);
            ChatIrcMessage ircMessage = new ChatIrcMessage(users.get(user), message);
            messages.add(ircMessage);
            return ircMessage;
        }
    }

    public InfoIrcMessage newInfoMessage(String message) {
        synchronized (messages) {
            InfoIrcMessage ircMessage = new InfoIrcMessage(message);
            messages.add(ircMessage);
            return ircMessage;
        }
    }

    /**
     * Marks message as read.
     *
     * @param message Message that will be marked as read
     */
    public void readMessage(ChatIrcMessage message) {
        message.read();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IrcChannel channel = (IrcChannel) o;
        return channelName.equals(channel.channelName);
    }

    @Override
    public int hashCode() {
        return channelName.hashCode();
    }
}
