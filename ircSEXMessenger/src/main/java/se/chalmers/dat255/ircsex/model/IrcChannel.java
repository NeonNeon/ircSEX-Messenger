package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;

/**
 * This class represents an IrcChannel and handles messages sent in it.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcChannel {

    private final String channelName;
    private final List<String> users;

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName - Name of channel
     */
    public IrcChannel(String channelName, List<String> users) {
        this.channelName = channelName;
        this.users = users;
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
     * Returns a list with the names of all users.
     *
     * @return - A list with all users
     */
    public List<String> getUsers() {
        return users;
    }
}
