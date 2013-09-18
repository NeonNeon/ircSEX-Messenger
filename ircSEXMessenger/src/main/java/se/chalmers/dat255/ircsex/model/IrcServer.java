package se.chalmers.dat255.ircsex.model;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;
import se.chalmers.dat255.ircsex.model.database.ChannelDatabaseAdapter;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer implements IrcProtocolAdapter.IrcProtocolServerListener {

    private final String host;
    private final int port;
    private final String login;
    private final String nick;
    private final String realName;

    private IrcProtocolAdapter protocol;

    private final ChannelDatabaseAdapter datasource;

    private final Map<String, IrcChannel> channels;
    private final Map<String, IrcChannel> connectedChannels;

    /**
     * Creates an IrcServer.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server username
     * @param nick - Nickname
     * @param realName - IRL name
     */
    public IrcServer(String host, int port, String login, String nick, String realName) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.nick = nick;
        this.realName = realName;

        channels = new HashMap<String, IrcChannel>();

        startProtocolAdapter(host, port, nick, login, realName);

        datasource = new ChannelDatabaseAdapter();
        datasource.open();

        connectedChannels = datasource.getAllIrcChannels();
    }

    /**
     * Start the protocol adapter.
     * This includes making a connection in a new thread and logging in with the specified login/nick.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server username
     * @param nick - Nickname
     * @param realName - IRL name
     */
    public void startProtocolAdapter(String host, int port, String nick, String login, String realName) {
        protocol = new IrcProtocolAdapter(host, port);
        protocol.addIrcProtocolServerListener(this);
        new Thread(protocol).start();
    }

    /**
     * Returns the serveraddress.
     *
     * @return Address to the server
     */
    public String getHost() {
        return host;
    }

    /**
     * Joins a channel on the specified server.
     *
     * @param channel - Name of channel to join
     */
    public void joinChannel(String channel) {
        protocol.joinChannel(channel);
    }

    /**
     * Joins a channel with password on the specified server.
     *
     * @param channel - Name of channel to join
     * @param key - Channel password
     */
    public void joinChannel(String channel, String key) {
        protocol.joinChannel(channel, key);
    }

    /**
     * Leaves a channel
     *
     * @param channel - Name of channel to leave
     */
    public void partChannel(String channel) {
        protocol.partChannel(channel);
    }

    @Override
    public void fireEvent(IrcProtocolAdapter.MessageType type, String message) {
        if (message == IrcProtocolAdapter.Messages.IOConnected) {
            protocol.connect(nick, login, realName);
        }
    }

    @Override
    public void fireChannelEvent(IrcProtocolAdapter.MessageType type, String channel, String message) {

    }
}
