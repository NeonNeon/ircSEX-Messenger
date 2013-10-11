package se.chalmers.dat255.ircsex.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;
import se.chalmers.dat255.ircsex.irc.IrcProtocolListener;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer implements IrcProtocolListener, NetworkStateHandler.ConnectionListener {

    private final String host;
    private final int port;
    private final String login;
    private final IrcUser user;
    private final String realName;
    private IrcChannel activeChannel;

    private final ChannelDAO datasource;
    private final ServerDAO serverDatasource;
    private final HighlightDAO highlightDatasource;

    private final ArrayList<SearchlistChannelItem> channels;
    private final ConcurrentMap<String, IrcChannel> connectedChannels;

    private final List<String> highlightsWords;
    private final List<IrcHighlight> highlights;
    private List<IrcHighlight> lastMessages;

    private IrcProtocolAdapter protocol;

    private List<SessionListener> sessionListeners;

    private boolean reconnecting;

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

        serverDatasource = new ServerDAO();
        serverDatasource.open();
        datasource = new ChannelDAO();
        datasource.open();
        highlightDatasource = new HighlightDAO();
        highlightDatasource.open();

        channels = new ArrayList<SearchlistChannelItem>();
        connectedChannels = new ConcurrentHashMap<String, IrcChannel>();

        highlightsWords = highlightDatasource.getHighlights();
        if (highlightsWords.size() == 0) {
            addHighlight(user.getNick());
        }
        highlights = new ArrayList<IrcHighlight>();
        lastMessages = new ArrayList<IrcHighlight>();

        NetworkStateHandler.addListener(this);
        NetworkStateHandler.start();
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
        if (NetworkStateHandler.isConnected()) {
            protocol.joinChannel(channel);
        }
    }

    /**
     * Joins a channel with password on the specified server.
     *
     * @param channel - Name of channel to join
     * @param key - Channel password
     */
    public void joinChannel(String channel, String key) {
        if (NetworkStateHandler.isConnected()) {
            protocol.joinChannel(channel, key);
        }
    }

    /**
     * Leaves a channel
     *
     * @param channel - Name of channel to leave
     */
    public void partChannel(String channel) {
        if (channel.contains("#")) {
            if (NetworkStateHandler.isConnected()) {
                protocol.partChannel(channel);
            }
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
        if (NetworkStateHandler.isConnected()) {
            protocol.disconnect(quitMessage);
        }
    }

    /**
     * Changes nickname.
     *
     * @param newNick - New nickname
     */
    public void changeNick(String newNick) {
        if (NetworkStateHandler.isConnected()) {
            protocol.setNick(newNick);
        }
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
        if (NetworkStateHandler.isConnected()) {
            protocol.sendChannelMessage(channel, message);
            connectedChannels.get(channel).newChatMessage(user.getNick(), message);
            ChatIrcMessage ircMessage = new ChatIrcMessage(user, message);
            for (SessionListener listener : sessionListeners) {
                listener.onSentMessage(host, channel, ircMessage);
            }
        }
    }

    /**
     * Sends whois command.
     *
     * @param user Username to lookup
     */
    public void whois(String user) {
        if (NetworkStateHandler.isConnected()) {
            user = IrcUser.extractUserName(user);
            protocol.whois(user);
        }
    }

    /**
     * Invites the specified user to the specified channel.
     *
     * @param user User to inviteUser
     * @param channel Channel to which the user will be invited
     */
    public void inviteUser(String user, IrcChannel channel) {
        if (NetworkStateHandler.isConnected()) {
            protocol.invite(user, channel.getChannelName());
        }
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

    /**
     * Adds a string to be highlighted on.
     *
     * @param str String to be highlighted on
     */
    public void addHighlight(String str) {
        highlightsWords.add(str);
        highlightDatasource.addHighlight(str);
    }

    /**
     * Removes a highlightstring.
     * @param str String to remove
     */
    public void removeHighlight(String str) {
        highlightsWords.remove(str);
        highlightDatasource.removeHighlight(str);
    }

    /**
     * Removes a highlightstring by index.
     * @param index Index to remove
     */
    public void removeHighlight(int index) {
        removeHighlight(highlightsWords.get(index));
    }

    /**
     * Returns all highlighted messages.
     *
     * @return Highlighted messages
     */
    public List<IrcHighlight> getHighlights() {
        return highlights;
    }

    public List<IrcHighlight> getLastMessages() {
        return lastMessages;
    }

    private IrcHighlight getHighlightByChannel(String channel) {
        for (IrcHighlight highlight : highlights) {
            if (highlight.getChannel().getChannelName().equals(channel)) {
                return highlight;
            }
        }
        return null;
    }

    private IrcHighlight getLastMessageByChannel(String channel) {
        for (IrcHighlight highlight : lastMessages) {
            if (highlight.getChannel().getChannelName().equals(channel)) {
                return highlight;
            }
        }
        return null;
    }

    private ArrayList<String> getHighlightChannels() {
        ArrayList<String> channels = new ArrayList<String>();
        for (IrcHighlight highlight : highlights) {
            channels.add(highlight.getChannel().getChannelName());
        }
        return channels;
    }

    private ArrayList<String> getLastMessageChannels() {
        ArrayList<String> channels = new ArrayList<String>();
        for (IrcHighlight highlight : lastMessages) {
            channels.add(highlight.getChannel().getChannelName());
        }
        return channels;
    }

    /**
     * Reads a highlight.
     *
     * @param highlight Highlight to mark as read
     */
    public void readHighlight(IrcHighlight highlight) {
        highlights.remove(highlight);
    }

    /**
     * Marks last message as read.
     */
    public void readLastMessage(IrcHighlight lastMessage) {
        lastMessages.remove(lastMessage);
    }

    public void readHighlight(String highlight) {
        readHighlight(getHighlightByChannel(highlight));
    }

    public void readLastMessage(String lastMessage) {
        readLastMessage(getLastMessageByChannel(lastMessage));
    }

    /**
     * Clears highlights.
     */
    public void clearHighlights() {
        highlights.clear();
    }

    public Set<String> getKnownUsers() {
        Set<String> users = new LinkedHashSet<String>();
        for (Map.Entry<String, IrcChannel> c : connectedChannels.entrySet()) {
            for (IrcUser user : c.getValue().getUsers()) {
                users.add(user.getNick());
            }
        }
        return users;
    }

    public ArrayList<SearchlistChannelItem> getChannels() {
        return channels;
    }

    public void listChannels() {
        protocol.listChannels();
    }

    public void setActiveChannel(IrcChannel activeChannel) {
        this.activeChannel = activeChannel;
    }

    private boolean checkHighlight(IrcChannel channel, String str) {
        for (String h : highlightsWords) {
            if (str.toLowerCase().contains(h.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void serverConnected() {
        if (NetworkStateHandler.isConnected()) {
            if (reconnecting) {
                protocol.disconnect("");
                reconnecting = false;
                startProtocolAdapter();
            } else {
                protocol.connect(user.getNick(), login, realName);
                for (SessionListener listener : sessionListeners) {
                    listener.onConnectionEstablished(host);
                }
            }
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
        }
        for (IrcChannel channel : connectedChannels.values()) {
            channel.nickChanged(oldNick, newNick);
            for (SessionListener listener : sessionListeners) {
                listener.onNickChange(host, channel.getChannelName(), new InfoIrcMessage(oldNick + " is now known as " + newNick));
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

            IrcChannel ircChannel = connectedChannels.get(channelName);
            ircChannel.userJoined(ircUser);
            for (SessionListener listener : sessionListeners) {
                IrcMessage ircMessage = ircChannel.newInfoMessage(nick + " has joined the channel");
                listener.onChannelUserJoin(host, channelName, ircMessage);
                listener.onChannelUserChange(host, channelName, ircChannel.getUsers());
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
            IrcChannel ircChannel = connectedChannels.get(channelName);
            ircChannel.userParted(nick);
            for (SessionListener listener : sessionListeners) {
                IrcMessage ircMessage = ircChannel.newInfoMessage(nick + " has left the channel");
                listener.onChannelUserPart(host, channelName, ircMessage);
                listener.onChannelUserChange(host, channelName, ircChannel.getUsers());
            }
        }
    }

    @Override
    public void userQuit(String nick, String quitMessage) {
        for (IrcChannel channel : connectedChannels.values()) {
            if (channel.hasUser(nick)) {
                channel.userParted(nick);
                String channelName = channel.getChannelName();
                for (SessionListener listener : sessionListeners) {
                    listener.onChannelUserChange(host, channelName, connectedChannels.get(channelName).getUsers());
                    listener.onChannelUserPart(host, channelName, new InfoIrcMessage(nick + " has left the channel"));
                }
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
            listener.whoisIdleTime(nick, IrcUser.formatIdleTime(seconds));
        }
    }

    @Override
    public void channelListResponse(String name, String topic, String users) {
        int userCount = Integer.parseInt(users);
        channels.add(new SearchlistChannelItem(name, userCount, topic));
    }

    @Override
    public void nickChangeError() {

    }

    @Override
    public void serverDisconnected() {
        if (NetworkStateHandler.isConnected()) {
            protocol = new IrcProtocolAdapter(host, port, this);
        }
    }

    @Override
    public void channelMessageReceived(String channel, String user, String message) {
        user = IrcUser.extractUserName(user);

        IrcChannel ircChannel = connectedChannels.get(channel);
        ChatIrcMessage ircMessage;
        if (!ircChannel.getChannelName().equals(activeChannel.getChannelName()) && checkHighlight(ircChannel, message)) {
            ircMessage = connectedChannels.get(channel).newChatMessage(user, message, true);
            if (getHighlightChannels().contains(ircChannel.getChannelName())) {
                readHighlight(ircChannel.getChannelName());
            }
            highlights.add(0, new IrcHighlight(ircChannel, ircMessage));
            if (getLastMessageChannels().contains((ircChannel.getChannelName()))) {
                readLastMessage(ircChannel.getChannelName());
            }
        } else if (!ircChannel.getChannelName().equals(activeChannel.getChannelName())) {
            ircMessage = connectedChannels.get(channel).newChatMessage(user, message);
            if (getLastMessageChannels().contains((ircChannel.getChannelName()))) {
                readLastMessage(ircChannel.getChannelName());
            }
            lastMessages.add(0, new IrcHighlight(ircChannel, ircMessage));
        } else {
            ircMessage = connectedChannels.get(channel).newChatMessage(user, message);
        }

        for (SessionListener listener : sessionListeners) {
            listener.onChannelMessage(host, channel, ircMessage);
            listener.onHighlight(ircChannel, ircMessage);
        }
    }

    @Override
    public void queryMessageReceived(String user, String message) {
        user = IrcUser.extractUserName(user);
        if (!connectedChannels.containsKey(user)) {
            queryUser(user);
        }

        IrcChannel ircChannel = connectedChannels.get(user);
        ChatIrcMessage ircMessage = connectedChannels.get(user).newChatMessage(user, message);
        if (!ircChannel.getChannelName().equals(activeChannel.getChannelName())) {
            if (getHighlightChannels().contains(ircChannel.getChannelName())) {
                readHighlight(ircChannel.getChannelName());
            }
            highlights.add(0, new IrcHighlight(ircChannel, ircMessage));
        }
        for (SessionListener listener : sessionListeners) {
            listener.onHighlight(ircChannel, ircMessage);
        }

        for (SessionListener listener : sessionListeners) {
            listener.onChannelMessage(host, user, ircMessage);
        }
    }

    @Override
    public void onOnline() {
        startProtocolAdapter();
    }

    @Override
    public void onOffline() {
    }
}
