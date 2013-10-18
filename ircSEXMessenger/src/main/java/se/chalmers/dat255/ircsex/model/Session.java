package se.chalmers.dat255.ircsex.model;

import android.content.Context;

import java.util.Map;

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

    private final ServerDAO datasource;

    /**
     * Creates an Session object.
     */
    private Session(Context context) {
        ContextHandler.CONTEXT = context;

        datasource = new ServerDAO();
        datasource.open();

        servers = datasource.getAllIrcServers();
    }

    private void addListener(WhoisListener listener) {
        for (IrcServer server : servers.values()) {
            if (listener != null) {
                if (listener instanceof SessionListener) {
                    server.addSessionListener((SessionListener) listener);
                } else {
                    server.addWhoisListener(listener);
                }
            }
        }
    }

    public void removeListener(WhoisListener listener) {
        for (IrcServer server : servers.values()) {
            if (listener instanceof SessionListener) {
                server.removeSessionListener((SessionListener) listener);
            } else {
                server.removeWhoisListener(listener);
            }
        }
    }

    public static Session getInstance() {
        if (instance == null) {
            throw new IllegalStateException("You're not allowed to call getInstance yet. There is no instance!");
        }
        return instance;
    }

    public static Session getInstance(Context context, WhoisListener listener) {
        if (instance == null) {
            instance = new Session(context);
        }
        instance.addListener(listener);
        return instance;
    }

    /**
     * Adds a server with a and connects to it.
     *
     * @param data all data used to connect
     * @param sessionListener
     */
    public void addServer(ServerConnectionData data, se.chalmers.dat255.ircsex.model.SessionListener sessionListener) {
        IrcServer ircServer = new IrcServer(data);
        servers.put(data.getServer(), ircServer);
        ircServer.addSessionListener(sessionListener);
        NetworkStateHandler.getInstance().notify(ircServer);
        datasource.addServer(data);
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
        if (channel.charAt(0) != '#') {
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
        if (this.activeChannel != null) {
            this.activeServer.setActiveChannel(this.activeChannel);
        }
    }

    public void changeNick(String newNick) {
        activeServer.changeNick(newNick);
    }

    public boolean containsServers() {
        return servers.size() > 0;
    }
}
