package se.chalmers.dat255.ircsex.model;

/**
 * Created by Wilhelm on 2013-09-19.
 */
public interface SessionListener {
    /**
     * Fired when the socked has been opened.
     * @param host Host to which the socket has been opened.
     */
    public void onConnectionEstablished(String host);

    /**
     * Registration completed with the server, other commands can be sent after this message has been received.
     * @param host Host on which the registration was completed.
     */
    public void onRegistrationCompleted(String host);

    /**
     *
     * @param host
     * @param message
     */
    public void onServerDisconnect(String host, String message);

    /**
     * The user joined a channel.
     * @param host
     * @param channelName
     */
    public void onServerJoin(String host, String channelName);

    /**
     * The user parted a channel.
     * @param host
     * @param channelName
     */
    public void onServerPart(String host, String channelName);

    /**
     * Another user joined the channel.
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelJoin(String host, String channel, String message);

    /**
     * Another user parted the channel.
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelPart(String host, String channel, String message);

    /**
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelMessage(String host, String channel, IrcMessage message);
}
