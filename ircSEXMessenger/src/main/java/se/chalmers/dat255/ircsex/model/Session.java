package se.chalmers.dat255.ircsex.model;

import android.content.Context;

import java.util.Map;

import se.chalmers.dat255.ircsex.model.database.ContextManager;
import se.chalmers.dat255.ircsex.model.database.ServerDatabaseAdapter;

/**
 * This class represents an IRC session. It lists and handles servers.
 *
 * Created by Oskar on 2013-09-17.
 */
public class Session {

    private static Session instance;

    private IrcServer activeServer;
    private IrcChannel activeChannel;
    private final Map<String, IrcServer> servers;

    private final ServerDatabaseAdapter datasource;
    private final SessionListener listener;

    /**
     * Creates an Session object.
     */
    private Session(Context context, SessionListener listener) {
        ContextManager.CHANNEL_CONTEXT = context;
        ContextManager.SERVER_CONTEXT = context;
        this.listener = listener;

        datasource = new ServerDatabaseAdapter();
        datasource.open();

        servers = datasource.getAllIrcServers();
    }

    private void addListener(SessionListener listener) {
        for (IrcServer server : servers.values()) {
            if (listener != null) {
                server.addSessionListener(listener);
            }
        }
    }

    public static Session getInstance(Context context, SessionListener listener) {
        if (instance == null) {
            instance = new Session(context, listener);
        }
        instance.addListener(listener);
        return instance;
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String nick, se.chalmers.dat255.ircsex.model.SessionListener sessionListener) {
        addServer(host, port, "banned", nick, sessionListener);
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String login, String nick, se.chalmers.dat255.ircsex.model.SessionListener sessionListener) {
        addServer(host, port, login, nick, "", sessionListener);
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     * @param realName - IRL name
     */
    public void addServer(String host, int port, String login, String nick, String realName, se.chalmers.dat255.ircsex.model.SessionListener sessionListener) {
        IrcServer ircServer = new IrcServer(host, port, login, nick, realName);
        servers.put(host, ircServer);
        ircServer.addSessionListener(sessionListener);
        NetworkStateHandler.notify(ircServer);
        datasource.addServer(host, port, login, nick, realName);
    }

    /**
     * Disconnects and removes a server.
     *
     * @param host - Server address
     */
    public void removeServer(String host) {
        servers.remove(host).quitServer("Client exited.");
    }

    /**
     * Joins a channel.
     *
     * @param host - Server address
     * @param channel - Name of channel to join
     * @param isChannel - True if an actual channel, false if query
     */
    public void joinChannel(String host, String channel, boolean isChannel) {
        if (isChannel && channel.charAt(0) != '#') {
            channel = "#" + channel;
        }
        servers.get(host).joinChannel(channel);
    }

    /**
     * Joins a channel.
     *
     * @param host - Server address
     * @param channel - Name of channel to join
     * @param key - Password to channel
     */
    public void joinChannel(String host, String channel, String key) {
        servers.get(host).joinChannel(channel, key);
    }

    /**
     * Leaves a channel
     *
     * @param host - Server address
     * @param channel - Name of channel to leave
     */
    public void partChannel(String host, String channel) {
        servers.get(host).partChannel(channel);
    }

    public IrcServer getActiveServer() {
        return activeServer;
    }

    public void setActiveServer(String activeServer) {
        this.activeServer = servers.get(activeServer);
    }

    public IrcChannel getActiveChannel() {
        return activeChannel;
    }

    public void setActiveChannel(String activeChannel) {
        this.activeChannel = activeServer.getConnectedChannel(activeChannel);
    }

    public void changeNick(String newNick) {
        activeServer.changeNick(newNick);
    }

    public boolean containsServers() {
        return servers.size() > 0;
    }
}
