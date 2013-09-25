package se.chalmers.dat255.ircsex.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;
import se.chalmers.dat255.ircsex.irc.IrcProtocolListener;
import se.chalmers.dat255.ircsex.model.database.ChannelDatabaseAdapter;
import se.chalmers.dat255.ircsex.model.database.ServerDatabaseAdapter;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer implements IrcProtocolListener {

    private final String host;
    private final int port;
    private final String login;
    private String nick;
    private final String realName;
    private boolean connected = false;

    private final ChannelDatabaseAdapter datasource;
    private final ServerDatabaseAdapter serverDatasource;

    private final Map<String, IrcChannel> channels;
    private final Map<String, IrcChannel> connectedChannels;

    private IrcProtocolAdapter protocol;

    private List<SessionListener> sessionListeners;

    public IrcServer(String host, int port, String nick) {
        this(host, port, nick, nick);
    }

    public IrcServer(String host, int port, String login, String nick) {
        this(host, port, login, nick, "");
    }

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

        sessionListeners = new ArrayList<SessionListener>();

        startProtocolAdapter();

        serverDatasource = new ServerDatabaseAdapter();
        serverDatasource.open();
        datasource = new ChannelDatabaseAdapter();
        datasource.open();

        channels = new HashMap<String, IrcChannel>();
        connectedChannels = new HashMap<String, IrcChannel>();
    }

    private void restoreChannels() {
        for (String channel : datasource.getIrcChannelsByServer(host)) {
            joinChannel(channel);
        }
    }

    /**
     * Start the protocol adapter.
     * This includes making a connection in a new thread and logging in with the specified login/nick.
     */
    public void startProtocolAdapter() {
        protocol = new IrcProtocolAdapter(host, port, this);
        new Thread(protocol).start();
    }

    /**
     * Returns the address of the server.
     *
     * @return Address to the server
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns a channel by string.
     *
     * @param channelName - Name of the channel to return
     * @return Requested channel
     */
    public IrcChannel getConnectedChannel(String channelName) {
        return connectedChannels.get(channelName);
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

    /**
     * Disconnects the server.
     *
     * @param quitMessage - Message to be shown to other users.
     */
    public void quitServer(String quitMessage) {
        protocol.disconnect(quitMessage);
    }

    /**
     * Changes nickname.
     *
     * @param newNick - New nickname
     */
    public void changeNick(String newNick) {
        protocol.setNick(newNick);
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

    @Override
    public void serverConnected() {
        protocol.connect(nick, login, realName);
        for (SessionListener listener : sessionListeners) {
            listener.onConnectionEstablished(host);
        }
    }

    @Override
    public void serverRegistered() {
        for (SessionListener listener : sessionListeners) {
            listener.onRegistrationCompleted(host);
        }

        restoreChannels();
    }

    @Override
    public void nickChanged(String oldNick, String newNick) {
        if (oldNick.equals(this.nick)) {
            nick = newNick;
            serverDatasource.updateNickname(host, newNick);
            for (SessionListener listener : sessionListeners) {
                listener.onNickChange(host, oldNick, newNick);
            }
        } else {
            for (IrcChannel channel : connectedChannels.values()) {
                channel.nickChanged(oldNick, newNick);
            }
        }
        for (IrcChannel channel : connectedChannels.values()) {
            channel.nickChanged(oldNick, newNick);
            for (SessionListener listener : sessionListeners) {
                listener.onChannelUserChange(host, channel.getChannelName(), channel.getUsers());
            }
        }
    }

    @Override
    public void usersInChannel(String channelName, List<String> users) {
        connectedChannels.get(channelName).addUsers(users);
        for (SessionListener listener : sessionListeners) {
            listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
        }
    }

    @Override
    public void userJoined(String channelName, String nick) {
        if (this.nick.equals(nick)) {
            IrcChannel channel = new IrcChannel(channelName);
            connectedChannels.put(channelName, channel);
            datasource.addChannel(host, channelName);
            for (SessionListener listener : sessionListeners) {
                listener.onServerJoin(host, channelName);
            }
        } else {
            connectedChannels.get(channelName).userJoined(nick);
            for (SessionListener listener : sessionListeners) {
                listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
            }
        }
    }

    @Override
    public void userParted(String channelName, String nick) {
        if (this.nick.equals(nick)) {
            connectedChannels.remove(channelName);
            datasource.removeChannel(channelName);
            for (SessionListener listener : sessionListeners) {
                listener.onServerPart(host, channelName);
            }
        } else {
            connectedChannels.get(channelName).userParted(nick);
            for (SessionListener listener : sessionListeners) {
                listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
            }
        }
    }

    @Override
    public void nickChangeError() {

    }

    @Override
    public void serverDisconnected() {
        protocol = new IrcProtocolAdapter(host, port, this);
    }

    @Override
    public void messageReceived(String channel, String user, String message, long timestamp) {
        connectedChannels.get(channel).newMessage(user, message, timestamp);
    }
}
