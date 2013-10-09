package se.chalmers.dat255.ircsex.irc;

import java.util.List;

/**
 * Created by oed on 9/23/13.
 * An interface that listens to events that are relevant to the IRC Server.
 */
public interface IrcProtocolListener {


    /**
     * Called when connection to the server has been established.
     */
    public void serverConnected();

    /**
     * Called when your are registered on the server.
     */
    public void serverRegistered();

    /**
     * Called when a user changed nick.
     * @param oldNick - the nick that the user had until now
     * @param newNick - the new nick of the user
     */
    public void nickChanged(String oldNick, String newNick);

    /**
     * This method gives you info about which users are in a specific channel.
     * @param channelName
     * @param users
     */
    public void usersInChannel(String channelName, List<String> users);

    /**
     * This method is called when a user joins a channel.
     * @param channelName
     * @param nick
     */
    public void userJoined(String channelName, String nick);

    /**
     * This method is called when a user parts a channel.
     * @param channelName
     * @param nick
     */
    public void userParted(String channelName, String nick);

    /**
     * This method is called whn a user quits from the server.
     * @param nick - the nick of the user
     * @param quitMessage - the quit message
     */
    public void userQuit(String nick, String quitMessage);

    /**
     * Sent when a channel message is received from the server.
     * @param channel - the channel that the message was sent to
     * @param user - the user that sent the message
     * @param message - the message sent
     */
    public void channelMessageReceived(String channel, String user, String message);

    /**
     * Sent when a query message is received from the server.
     * @param user - the user that sent the message
     * @param message - the message sent
     */
    public void queryMessageReceived(String user, String message);

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
     * This method sends the nicks idletime in seconds as a whois request resopnse.
     * @param nick
     * @param seconds
     */
    public void whoisIdleTime(String nick, int seconds);

    /**
     * This is sent as a response to a listChannel call to IPA.
     * @param name - the name of the channel
     * @param topic - the topic of the channel
     */
    public void channelListResponse(String name, String topic);

    // ERRORS

    /**
     * Something went wrong when you tried to change nick.
     */
    public void nickChangeError();

    /**
     * The server was disconnected.
     * To reconnect get a new IPA.
     */
    public void serverDisconnected();

}
