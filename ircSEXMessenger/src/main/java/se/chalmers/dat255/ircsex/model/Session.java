package se.chalmers.dat255.ircsex.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.dat255.ircsex.model.database.ContextManager;
import se.chalmers.dat255.ircsex.model.database.ServerDatabaseAdapter;

/**
 * This class represents an IRC session. It lists and handles servers.
 *
 * Created by Oskar on 2013-09-17.
 */
public class Session {
    private IrcServer activeServer;
    private IrcChannel activeChannel;
    private final Map<String, IrcServer> servers;

    private final ServerDatabaseAdapter datasource;

    /**
     * Creates an Session object.
     */
    public Session(Context context) {
        ContextManager.CHANNEL_CONTEXT = context;
        ContextManager.SERVER_CONTEXT = context;

        datasource = new ServerDatabaseAdapter();
        datasource.open();

        //disabled due to no ui support
        //servers = datasource.getAllIrcServers();
        //Initialize map when above code is commented
        servers = new HashMap<String, IrcServer>();
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String nick, SessionListener sessionListener) {
        addServer(host, port, "banned", nick, sessionListener); // TODO: Should not be banned before release.
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String login, String nick, SessionListener sessionListener) {
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
    public void addServer(String host, int port, String login, String nick, String realName, SessionListener sessionListener) {
        IrcServer ircServer = new IrcServer(host, port, login, nick, realName);
        servers.put(host, ircServer);
        ircServer.addSessionListener(sessionListener);
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
     */
    public void joinChannel(String host, String channel) {
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

    /**
     * Adds a listener.
     *
     * @param listener - Listener to add
     */
    public void addListener(SessionListener listener) {
        for (IrcServer server : servers.values()) {
            server.addSessionListener(listener);
        }
    }

    /**
     * Removes a listener.
     *
     * @param listener - Listener to remove
     */
    public void removeListener(SessionListener listener) {
        for (IrcServer server : servers.values()) {
            server.removeSessionListener(listener);
        }
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

    /**
     * Enum to describe events in session.
     *
     * SERVER_CONNECTION_ESTABLISHED - Socket opened
     * SERVER_REGISTRATION_COMPLETED - Registration completed with the server,
     *      other commands can be sent after this message has been received.
     * SERVER_DISCONNECT - Disconnected from a server
     * SERVER_JOIN - Joined a channel
     * SERVER_PART - Left a channel
     * CHANNEL_JOIN - New user in a channel
     * CHANNEL_PART - An user left the channel
     * CHANNEL_MESSAGE - New message in a channel
     */
    public enum SessionEvent {
        SERVER_CONNECTION_ESTABLISHED,
        SERVER_REGISTRATION_COMPLETED,
        SERVER_DISCONNECT,
        SERVER_JOIN,
        SERVER_PART,
        CHANNEL_JOIN,
        CHANNEL_PART,
        CHANNEL_MESSAGE
    }

    /**
     * Interface for listeners of the Session object.
     */
    public interface SessionListener {
        public void fireSessionEvent(SessionEvent event, String message);
    }
}
