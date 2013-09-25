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

    public void messageReceived(String channel, String user, String message, long timestamp);
}
