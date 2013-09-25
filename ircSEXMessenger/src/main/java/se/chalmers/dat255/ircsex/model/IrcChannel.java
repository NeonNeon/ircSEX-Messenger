package se.chalmers.dat255.ircsex.model;

import java.util.Collection;
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

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName - Name of channel
     */
    public IrcChannel(String channelName) {
        this.channelName = channelName;
        this.users = new HashMap<String, IrcUser>();
    }

    /**
     * Returns the name of the channel.
     *
     * @return - Name of the channel
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
            this.users.put(user, new IrcUser(user));
        }
    }

    /**
     * Adds a user to the list of users.
     *
     * @param user - The user who joined
     */
    public void userJoined(String user) {
        users.put(user, new IrcUser(user));
    }

    /**
     * Removes a user from the list of users.
     *
     * @param user - The user who left
     */
    public void userParted(String user) {
        users.remove(user);
    }

    /**
     * Returns a list with the names of all users.
     *
     * @return - A list with all users
     */
    public Collection<IrcUser> getUsers() {
        return users.values();
    }
}
