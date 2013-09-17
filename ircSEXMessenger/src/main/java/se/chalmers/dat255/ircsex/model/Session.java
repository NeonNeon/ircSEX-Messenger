package se.chalmers.dat255.ircsex.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an IRC session. It lists and handles servers.
 *
 * Created by Oskar on 2013-09-17.
 */
public class Session {
    private Map<String, IrcServer> servers;

    /**
     * Creates an Session object.
     */
    public Session() {
        servers = new HashMap<String, IrcServer>();
    }

    /**
     * Adds a server and connects to it.
     *
     * @param server - Server address
     * @param port - Server port
     * @param nick - Nickname
     */
    public void addServer(String server, int port, String nick) {
        servers.put(server, new IrcServer(server, port, nick));
    }

    /**
     * Adds a server and connects to it.
     *
     * @param server - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     */
    public void addServer(String server, int port, String login, String nick) {
        servers.put(server, new IrcServer(server, port, login, nick));
    }

    /**
     * Adds a server and connects to it.
     *
     * @param server - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     * @param realName - IRL name
     */
    public void addServer(String server, int port, String login, String nick, String realName) {
        servers.put(server, new IrcServer(server, port, login, nick, realName));
    }

    /**
     * Disconnects and removes a server.
     *
     * @param host - Server address
     */
    public void removeServer(String host) {
        servers.remove(host);
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
}
