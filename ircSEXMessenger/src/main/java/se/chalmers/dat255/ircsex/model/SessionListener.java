package se.chalmers.dat255.ircsex.model;

import java.util.Collection;

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
     * User disconnected the server.
     *
     * @param host server adress
     */
    public void onDisconnect(String host);

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
     * @param users
     */
    public void onChannelUserChange(String host, String channel, Collection<IrcUser> users);

    /**
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelMessage(String host, String channel, String message);

    /**
     * Notifies the ui when a user has changed nick.
     *
     * @param oldNick
     * @param newNick
     */
    public void onNickChange(String host, String oldNick, String newNick);
}
