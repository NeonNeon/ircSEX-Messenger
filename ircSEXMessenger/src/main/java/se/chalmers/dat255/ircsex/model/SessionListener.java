package se.chalmers.dat255.ircsex.model;

import java.util.List;

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
    public void onChannelUserChange(String host, String channel, List<IrcUser> users);

    /**
     * Notifies the ui when a user has joined the channel
     * @param host
     * @param channel
     * @param user
     */
    public void onChannelUserJoin(String host, String channel, IrcUser user);

    /**
     * Notifies the ui when a user has left the channel
     * @param host
     * @param channel
     * @param nick
     */
    public void onChannelUserPart(String host, String channel, String nick);

    /**
     * Notifies the ui when a user has changed nick.
     *
     * @param oldNick
     * @param newNick
     */
    public void onNickChange(String host, String oldNick, String newNick);

    /**
     * Receiving a message from another user.
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onChannelMessage(String host, String channel, ChatIrcMessage message);

    /**
     * Receiving message as confirmation of a sent message.
     *
     * @param host
     * @param channel
     * @param message
     */
    public void onSentMessage(String host, String channel, ChatIrcMessage message);

    /**
     * This method sends a list of connected channels as a whois request resopnse.
     * @param nick - the nick of the requested whois
     * @param channels - the channels the nick is connected to
     */
    public void whoisChannels(String nick, List<String> channels);

    /**
     * This method sends the nicks realname as a whois request resopnse.
     * @param nick
     * @param realname
     */
    public void whoisRealname(String nick, String realname);

    /**
     * This method sends the nicks idletime as a whois request resopnse.
     * @param nick
     * @param formattedIdleTime
     */
    public void whoisIdleTime(String nick, String formattedIdleTime);
}
