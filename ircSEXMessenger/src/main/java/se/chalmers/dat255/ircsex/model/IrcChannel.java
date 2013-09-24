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

    private final List<IrcMessage> readMessages;
    private final List<IrcMessage> unreadMessages;

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName - Name of channel
     */
    public IrcChannel(String channelName) {
        this.channelName = channelName;
        users = new ArrayList<String>();

        readMessages = new ArrayList<IrcMessage>();
        unreadMessages = new ArrayList<IrcMessage>();
    }

    /**
     * Returns the name of the channel.
     *
     * @return - Name of the channel
     */
    public String getChannelName() {
        return channelName;
    }
}
