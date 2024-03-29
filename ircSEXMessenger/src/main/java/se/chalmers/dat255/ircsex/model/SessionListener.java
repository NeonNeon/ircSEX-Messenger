package se.chalmers.dat255.ircsex.model;

import java.util.List;

/**
 * Created by Wilhelm on 2013-09-19.
 */
public interface SessionListener extends WhoisListener {
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
     */
    public void onServerDisconnect(String host);

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
    public void onChannelUserChange(String host, String channel, List<IrcUser> users);

    /**
     * Notifies the ui when a user has joined the channel
     * @param host
     * @param channel
     * @param joinMessage
     */
    public void onChannelUserJoin(String host, String channel, IrcMessage joinMessage);

    /**
     * Notifies the ui when a user has left the channel
     * @param host
     * @param channel
     * @param partMessage
     */
    public void onChannelUserPart(String host, String channel, IrcMessage partMessage);

    /**
     * Notifies the ui when a user has changed nick.
     * @param channel
     * @param ircMessage the resulting chat message
     */
    public void onNickChange(String host, String channel, IrcMessage ircMessage);

    /**
     * Receiving a message from another user.
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelMessage(String host, String channel, ChatIrcMessage message);

    /**
     * Receiving a hilightnotification.
     */
    public void onHighlightChange();

    /**
     * Receiving message as confirmation of a sent message.
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onSentMessage(String host, String channel, ChatIrcMessage message);

    /**
     * Something went wrong while sending a query.
     * @param message Error message
     */
    public void queryError(String message);

    /**
     * Something went wrong while logging in.
     * @param message Error message
     */
    public void loginError(String message);

    /**
     * Something went wrong while joining a channel.
     * @param message Error message
     */
    public void channelJoinError(String message);

    /**
     * Something went wrong when you tried to change nick.
     * @param message Error message
     */
    public void nickChangeError(String message);

    /**
     * Something went wrong while inviting a user.
     * @param message Error message
     */
    public void inviteError(String message);

    /**
     * Called on failure to connect to the server
     */
    public void serverConnectionError();
}
