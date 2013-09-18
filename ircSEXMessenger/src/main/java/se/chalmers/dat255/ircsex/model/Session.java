package se.chalmers.dat255.ircsex.model;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import se.chalmers.dat255.ircsex.model.db.IrcServerDataSource;

/**
 * This class represents an IRC session. It lists and handles servers.
 *
 * Created by Oskar on 2013-09-17.
 */
public class Session {

    private Map<String, IrcServer> servers;

    private IrcServerDataSource datasource;

    /**
     * Creates an Session object.
     */
    public Session(Context context) {
        servers = new HashMap<String, IrcServer>();

        datasource = new IrcServerDataSource(context);
        datasource.open();

        //disabled due to no ui support
        //servers = datasource.getAllIrcServers();
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String nick) {
        addServer(host, port, nick, nick);
    }

    /**
     * Adds a server and connects to it.
     *
     * @param host - Server address
     * @param port - Server port
     * @param login - Server login username
     * @param nick - Nickname
     */
    public void addServer(String host, int port, String login, String nick) {
        addServer(host, port, login, nick, "");
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
    public void addServer(String host, int port, String login, String nick, String realName) {
        servers.put(host, new IrcServer(host, port, login, nick, realName));
        datasource.addServer(host, port, login, nick, realName);
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
