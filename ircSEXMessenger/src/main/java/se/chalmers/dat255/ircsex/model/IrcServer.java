package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private List<SessionListener> sessionListeners;

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

        sessionListeners = new ArrayList<SessionListener>();

        startProtocolAdapter();

        datasource = new ChannelDatabaseAdapter();
        datasource.open();

        connectedChannels = new HashMap<String, IrcChannel>();
    }

    /**
     * Start the protocol adapter.
     * This includes making a connection in a new thread and logging in with the specified login/nick.
     */
    public void startProtocolAdapter() {
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

    public IrcChannel getConnectedChannel(String string) {
        return connectedChannels.get(string);
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

    public void quitServer(String quitMessage) {
        protocol.removeIrcProtocolServerListener(this);
        protocol.disconnect(quitMessage);
    }

    @Override
    public void fireEvent(IrcProtocolAdapter.MessageType type, String message) {
        switch (type) {
            case NORMAL:
                if (message == IrcProtocolAdapter.Messages.IOConnected) {
                    protocol.connect(nick, login, realName);
                    onServerConnectionEstablished();
                }
                break;
            case SERVER_REGISTERED:
                onServerRegistrationCompleted();
                break;
            case ERROR:
                break;
            case JOIN:
                IrcChannel channel = new IrcChannel(message);
                connectedChannels.put(message, channel);
                datasource.addChannel(host, message);
                onServerJoin(message);
                break;
            case PART:
                connectedChannels.remove(message);
                datasource.removeChannel(message);
                onServerPart(message);
                break;
        }
    }

    @Override
    public void fireChannelEvent(IrcProtocolAdapter.MessageType type, String channel, String message) {

    }

    /**
     * Add a listener to everything that the session object handles.
     *
     * @param listener - Listener to add
     */
    public void addSessionListener(SessionListener listener) {
        sessionListeners.add(listener);
    }

    /**
     * Removes a SessionListener.
     *
     * @param listener - Listener to remove
     */
    public void removeSessionListener(SessionListener listener) {
        sessionListeners.remove(listener);
    }

    private void onServerConnectionEstablished() {
        for (SessionListener listener : sessionListeners) {
            listener.onConnectionEstablished(host);
        }
    }

    private void onServerRegistrationCompleted() {
        for (SessionListener listener : sessionListeners) {
            listener.onRegistrationCompleted(host);
        }
    }

    private void onServerJoin(String channelName) {
        for (SessionListener listener : sessionListeners) {
            listener.onServerJoin(host, channelName);
        }
    }

    private void onServerPart(String channelName) {
        for (SessionListener listener : sessionListeners) {
            listener.onServerPart(host, channelName);
        }
    }
}
