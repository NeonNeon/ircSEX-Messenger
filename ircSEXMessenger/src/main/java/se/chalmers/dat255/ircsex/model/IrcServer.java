package se.chalmers.dat255.ircsex.model;

import android.util.Log;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private final IrcUser user;
    private final String realName;
    private boolean connected = false;

    private final ChannelDatabaseAdapter datasource;
    private final ServerDatabaseAdapter serverDatasource;

    private final ConcurrentMap<String, IrcChannel> channels;
    private final ConcurrentMap<String, IrcChannel> connectedChannels;

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
        this.user = new IrcUser(nick, IrcUser.NO_STATUS);
        this.user.setSelf();
        this.realName = realName;

        sessionListeners = new ArrayList<SessionListener>();

        startProtocolAdapter();

        serverDatasource = new ServerDatabaseAdapter();
        serverDatasource.open();
        datasource = new ChannelDatabaseAdapter();
        datasource.open();

        channels = new ConcurrentHashMap<String, IrcChannel>();
        connectedChannels = new ConcurrentHashMap<String, IrcChannel>();
    }

    private void restoreChannels() {
        for (String channel : datasource.getIrcChannelsByServer(host)) {
            if (channel.contains("#")) {
                joinChannel(channel);
            } else {
                queryUser(channel);
            }
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
        if (channel.contains("#")) {
            protocol.partChannel(channel);
        } else {
            userParted(channel, user.getNick());
        }
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

    /**
     * Sends a message to the active channel.
     *
     * @param message Message to send
     */
    public void sendMessage(String channel, String message) {
        protocol.sendChannelMessage(channel, message);
        connectedChannels.get(channel).newMessage(user.getNick(), message);
        IrcMessage ircMessage = new IrcMessage(user, message);
        for (SessionListener listener : sessionListeners) {
            listener.onSentMessage(host, channel, ircMessage);
        }
    }

    /**
     * Sends whois command.
     *
     * @param user Username to lookup
     */
    public void whois(String user) {
        user = IrcUser.extractUserName(user);
        protocol.whois(user);
    }

    /**
     * Invites the specified user to the specified channel.
     *
     * @param user User to inviteUser
     * @param channel Channel to which the user will be invited
     */
    public void inviteUser(String user, IrcChannel channel) {
        protocol.invite(user, channel.getChannelName());
    }

    /**
     * Returns the IrcUser for the user on this server.
     * @return The user
     */
    public IrcUser getUser() {
        return user;
    }

    /**
     * Opens a private chat.
     * @param user User to chat with
     */
    public void queryUser(String user) {
        if (!connectedChannels.containsKey(user)) {
            userJoined(user, this.user.getNick());
        }
    }

    private Set<IrcUser> getKnownUsers() {
        Set<IrcUser> users = new LinkedHashSet<>();
        for (Map.Entry<String, IrcChannel> c : connectedChannels.entrySet()) {
            for (IrcUser user : c.getValue().getUsers()) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Returns a set of IrcUser which contains searchTerm.
     * @param searchTerm - the term to search for
     * @return all matching users
     */
    public Set<IrcUser> searchUsers(String searchTerm) {
        Set<IrcUser> users = getKnownUsers();
        for (IrcUser user : users) {
            if (!user.getNick().contains(searchTerm)) {
                users.remove(user);
            }
        }
        return users;
    }

    @Override
    public void serverConnected() {
        protocol.connect(user.getNick(), login, realName);
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
        if (user.isNamed(oldNick)) {
            user.changeNick(newNick);
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
        for (String nick : users) {
            char status = IrcUser.extractUserStatus(nick);
            nick = IrcUser.extractUserName(nick);
            IrcUser user = new IrcUser(nick, status);
            if (user.equals(this.user)) {
                user.setSelf();
            }
            connectedChannels.get(channelName).userJoined(user);
        }


        for (SessionListener listener : sessionListeners) {
            listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
        }
    }

    @Override
    public void userJoined(String channelName, String nick) {
        if (this.user.getNick().equals(nick)) {
            IrcChannel channel = new IrcChannel(channelName);
            if (!channelName.contains("#")) {
                channel.userJoined(user);
                channel.userJoined(new IrcUser(channelName, IrcUser.NO_STATUS));
            }
            connectedChannels.put(channelName, channel);
            datasource.addChannel(host, channelName);
            for (SessionListener listener : sessionListeners) {
                listener.onServerJoin(host, channelName);
            }
        } else {
            char status = IrcUser.extractUserStatus(nick);
            IrcUser ircUser = new IrcUser(IrcUser.extractUserName(nick), status);
            if (ircUser.equals(this.user)) {
                ircUser.setSelf();
            }

            connectedChannels.get(channelName).userJoined(ircUser);
            for (SessionListener listener : sessionListeners) {
                listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
            }
        }
    }

    @Override
    public void userParted(String channelName, String nick) {
        if (user.isNamed(nick)) {
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
    public void whoisChannels(String nick, List<String> channels) {
        for (SessionListener listener : sessionListeners) {
            listener.whoisChannels(nick, channels);
        }
    }

    @Override
    public void whoisRealname(String nick, String realname) {
        for (SessionListener listener : sessionListeners) {
            listener.whoisRealname(nick, realname);
        }
    }

    @Override
    public void whoisIdleTime(String nick, int seconds) {
        for (SessionListener listener : sessionListeners) {
            listener.whoisIdleTime(nick, seconds);
        }
    }

    @Override
    public void channelListResponse(String name, String topic) {

    }

    @Override
    public void nickChangeError() {

    }

    @Override
    public void serverDisconnected() {
        protocol = new IrcProtocolAdapter(host, port, this);
    }

    @Override
    public void channelMessageReceived(String channel, String user, String message) {
        user = IrcUser.extractUserName(user);

        IrcMessage ircMessage = connectedChannels.get(channel).newMessage(user, message);

        for (SessionListener listener : sessionListeners) {
            listener.onChannelMessage(host, channel, ircMessage);
        }
    }

    @Override
    public void queryMessageReceived(String user, String message) {
        user = IrcUser.extractUserName(user);
        IrcMessage ircMessage = connectedChannels.get(user).newMessage(user, message);
        for (SessionListener listener : sessionListeners) {
            listener.onChannelMessage(host, user, ircMessage);
        }
    }
}
