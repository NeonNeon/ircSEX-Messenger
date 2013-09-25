package se.chalmers.dat255.ircsex.irc;

import java.util.List;

/**
 * Created by oed on 9/23/13.
 * An interface that listens to events that are relevant to the IRC Server.
 */
public interface IrcProtocolListener {


    public void serverConnected();

    public void serverRegistered();

    public void joinedChannel(String channelName);

    public void partedChannel(String channelName);

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

    public void nickChangeError();

    public void serverDisconnected();

    public void messageReceived(String channel, String user, String message, long timestamp);
}
