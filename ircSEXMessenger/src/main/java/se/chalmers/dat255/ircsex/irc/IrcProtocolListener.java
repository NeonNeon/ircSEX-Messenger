package se.chalmers.dat255.ircsex.irc;

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
     * Called when you join a channel.
     * @param channelName - the name of the channel you joined
     */
    public void joinedChannel(String channelName);

    /**
     * Called when you part a channel.
     * @param channelName - - the name of the channel you parted
     */
    public void partedChannel(String channelName);

    /**
     * Called when a user changed nick.
     * @param oldNick - the nick that the user had until now
     * @param newNick - the new nick of the user
     */
    public void nickChanged(String oldNick, String newNick);

    // ERRORS

    /**
     * Something went wrong when you tried to change nick.
     */
    public void nickChangeError();

    /**
     * The server was disconnected.
     * To reconnect get a new IPA.
     */
    public void ServerDisconnected();
}
