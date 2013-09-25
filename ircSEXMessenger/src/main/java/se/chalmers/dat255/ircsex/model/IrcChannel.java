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

    private final List<IrcMessage> messages;

    /**
     * Creates an IrcChannel object.
     *
     * @param channelName Name of channel
     */
    public IrcChannel(String channelName) {
        this.channelName = channelName;
        users = new ArrayList<String>();

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
     * @return The IrcMessage created from the message string and user string
     */
    public IrcMessage newMessage(String user, String message) {
        IrcMessage ircMessage = new IrcMessage(user, message);
        messages.add(ircMessage);
        return ircMessage;
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
