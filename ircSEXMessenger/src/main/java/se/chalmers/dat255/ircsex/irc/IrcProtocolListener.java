package se.chalmers.dat255.ircsex.irc;

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

    // ERRORS

    public void nickChangeError();

    public void ServerDisconnected();

    public void messageReceived(String channel, String user, String message, long timestamp);
}
