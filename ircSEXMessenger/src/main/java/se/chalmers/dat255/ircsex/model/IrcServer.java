package se.chalmers.dat255.ircsex.model;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;
import se.chalmers.dat255.ircsex.irc.IrcProtocolListener;
import se.chalmers.dat255.ircsex.irc.IrcProtocolStrings;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer implements IrcProtocolListener, NetworkStateHandler.ConnectionListener {

    private final IrcUser user;
    private IrcChannel activeChannel;
    private boolean firstConnect;
    private boolean connected;

    private final ChannelDAO channelDAO;
    private final ServerDAO serverDAO;
    private final HighlightDAO highlightDAO;

    private final ArrayList<SearchlistChannelItem> channels;
    private final ConcurrentMap<String, IrcChannel> connectedChannels;
    private final ServerConnectionData serverConnectionData;

    private final List<String> highlightsWords;
    private final List<IrcHighlight> highlights;
    private List<IrcHighlight> lastMessages;

    private IrcProtocolAdapter protocol;

    private List<SessionListener> sessionListeners;
    private List<WhoisListener> whoisListeners;

    private boolean disconnecting;
    private NetworkStateHandler networkStateHandler;

    /**
     * Creates an IrcServer.
     *
     * @param data
     */
    public IrcServer(ServerConnectionData data) {
        this.serverConnectionData = data;
        this.user = new IrcUser(data.getNickname(), IrcUser.NO_STATUS);
        this.user.setSelf();

        sessionListeners = new ArrayList<SessionListener>();
        whoisListeners = new ArrayList<WhoisListener>();

        serverDAO = new ServerDAO();
        serverDAO.open();
        channelDAO = new ChannelDAO();
        channelDAO.open();
        highlightDAO = new HighlightDAO();
        highlightDAO.open();

        channels = new ArrayList<SearchlistChannelItem>();
        connectedChannels = new ConcurrentHashMap<String, IrcChannel>();

        highlightsWords = highlightDAO.getHighlights();
        if (highlightsWords.size() == 0) {
            addHighlight(user.getNick());
        }
        highlights = new ArrayList<IrcHighlight>();
        lastMessages = new ArrayList<IrcHighlight>();

        networkStateHandler = NetworkStateHandler.getInstance();
        networkStateHandler.addListener(this);
        networkStateHandler.notify(this);
    }

    private void restoreChannels() {
        for (String channel : channelDAO.getIrcChannelsByServer(serverConnectionData.getServer())) {
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
        protocol = Brewery.getIPA(serverConnectionData, this);
       // protocol = Brewery.getSSHIPA("levelinver.se", "ircsex", "l", "localhost", 4444, this);
        new Thread(protocol).start();
    }

    /**
     * Returns the address of the server.
     *
     * @return Address to the server
     */
    public String getHost() {
        return serverConnectionData.getServer();
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
        if (networkStateHandler.isConnected()) {
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
        if (networkStateHandler.isConnected()) {
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
            if (networkStateHandler.isConnected()) {
                protocol.partChannel(channel);
                channelDAO.removeChannel(channel);
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
        if (networkStateHandler.isConnected()) {
            disconnecting = true;
            protocol.disconnect(quitMessage);
            serverDAO.removeServer(getHost());
            for (SessionListener listener : sessionListeners) {
                listener.onServerDisconnect(serverConnectionData.getServer(), quitMessage);
            }
        }
    }

    /**
     * Changes nickname.
     *
     * @param newNick - New nickname
     */
    public void changeNick(String newNick) {
        if (networkStateHandler.isConnected()) {
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
        addWhoisListener(listener);
    }

    public void addWhoisListener(WhoisListener listener) {
        whoisListeners.add(listener);
    }

    /**
     * Removes a SessionListener.
     *
     * @param listener - Listener to remove
     */
    public void removeSessionListener(SessionListener listener) {
        sessionListeners.remove(listener);
        removeWhoisListener(listener);
    }

    public void removeWhoisListener(WhoisListener listener) {
        whoisListeners.remove(listener);
    }

    /**
     * Sends a message to the active channel.
     *
     * @param message Message to send
     */
    public void sendMessage(String channel, String message) {
        if (networkStateHandler.isConnected()) {
            protocol.sendChannelMessage(channel, message);
            connectedChannels.get(channel).newChatMessage(user.getNick(), message);
            ChatIrcMessage ircMessage = new ChatIrcMessage(user, message);
            for (SessionListener listener : sessionListeners) {
                listener.onSentMessage(serverConnectionData.getServer(), channel, ircMessage);
            }
        }
    }

    /**
     * Sends whois command.
     *
     * @param user Username to lookup
     */
    public void whois(String user) {
        if (networkStateHandler.isConnected()) {
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
        if (networkStateHandler.isConnected()) {
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
        highlightDAO.addHighlight(str);
    }

    public List<String> getHighlightWords() {
        return highlightsWords;
    }

    /**
     * Removes a highlightstring.
     * @param str String to remove
     */
    public void removeHighlight(String str) {
        highlightsWords.remove(str);
        highlightDAO.removeHighlight(str);
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

        for (SessionListener listener : sessionListeners) {
            listener.onHighlightChange();
        }
    }

    /**
     * Marks last message as read.
     */
    public void readLastMessage(IrcHighlight lastMessage) {
        lastMessages.remove(lastMessage);

        for (SessionListener listener : sessionListeners) {
            listener.onHighlightChange();
        }
    }

    public void readHighlight(String highlight) {
        readHighlight(getHighlightByChannel(highlight));
    }

    public void readLastMessage(String lastMessage) {
        readLastMessage(getLastMessageByChannel(lastMessage));
    }

    private void newHighlight(IrcHighlight highlight) {
        highlights.add(0, highlight);

        for (SessionListener listener : sessionListeners) {
            listener.onHighlightChange();
        }
    }

    private void newLastMessage(IrcHighlight lastmessage) {
        lastMessages.add(0, lastmessage);

        for (SessionListener listener : sessionListeners) {
            listener.onHighlightChange();
        }
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
        if (networkStateHandler.isConnected()) {
            channels.clear();
            protocol.listChannels();
        }
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
        if (networkStateHandler.isConnected()) {
            connect();
            disconnecting = false;
            for (SessionListener listener : sessionListeners) {
                listener.onConnectionEstablished(serverConnectionData.getServer());
            }
        }
    }

    private void connect() {
        if(serverConnectionData.getPassword().equals("")) {
            protocol.connect(user.getNick(), serverConnectionData.getLogin(),
                    serverConnectionData.getRealname());
        } else {
            protocol.connect(user.getNick(), serverConnectionData.getLogin(),
                    serverConnectionData.getRealname(), serverConnectionData.getPassword());
        }
    }

    @Override
    public void serverRegistered(String server, String nick) {
        connected = true;
        firstConnect = true;

        for (SessionListener listener : sessionListeners) {
            listener.onRegistrationCompleted(serverConnectionData.getServer());
        }

        restoreChannels();
    }

    @Override
    public void nickChanged(String oldNick, String newNick) {
        if (user.isNamed(oldNick)) {
            user.changeNick(newNick);
            removeHighlight(oldNick);
            addHighlight(newNick);
            serverConnectionData.setNickname(newNick);
            serverDAO.updateNickname(serverConnectionData.getServer(), newNick);
            serverConnectionData.setNickname(newNick);
        }
        for (IrcChannel channel : connectedChannels.values()) {
            channel.nickChanged(oldNick, newNick);
            for (SessionListener listener : sessionListeners) {
                listener.onNickChange(serverConnectionData.getServer(), channel.getChannelName(), new InfoIrcMessage(oldNick + " is now known as " + newNick));
                listener.onChannelUserChange(serverConnectionData.getServer(), channel.getChannelName(), channel.getUsers());
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
            listener.onChannelUserChange(serverConnectionData.getServer(), channelName, connectedChannels.get(channelName).getUsers());
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
            channelDAO.addChannel(serverConnectionData.getServer(), channelName);
            for (SessionListener listener : sessionListeners) {
                listener.onServerJoin(serverConnectionData.getServer(), channelName);
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
                listener.onChannelUserJoin(serverConnectionData.getServer(), channelName, ircMessage);
                listener.onChannelUserChange(serverConnectionData.getServer(), channelName, ircChannel.getUsers());
            }
        }
    }

    @Override
    public void userParted(String channelName, String nick) {
        if (user.isNamed(nick)) {
            connectedChannels.remove(channelName);
            channelDAO.removeChannel(channelName);
            for (SessionListener listener : sessionListeners) {
                listener.onServerPart(serverConnectionData.getServer(), channelName);
            }
        } else {
            IrcChannel ircChannel = connectedChannels.get(channelName);
            ircChannel.userParted(nick);
            for (SessionListener listener : sessionListeners) {
                IrcMessage ircMessage = ircChannel.newInfoMessage(nick + " has left the channel");
                listener.onChannelUserPart(serverConnectionData.getServer(), channelName, ircMessage);
                listener.onChannelUserChange(serverConnectionData.getServer(), channelName, ircChannel.getUsers());
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
                    listener.onChannelUserChange(serverConnectionData.getServer(), channelName, connectedChannels.get(channelName).getUsers());
                    listener.onChannelUserPart(serverConnectionData.getServer(), channelName, new InfoIrcMessage(nick + " has left the channel"));
                }
            }
        }
    }

    @Override
    public void whoisChannels(String nick, List<String> channels) {
        for (WhoisListener listener : whoisListeners) {
            listener.whoisChannels(nick, channels);
        }
    }

    @Override
    public void whoisRealname(String nick, String realname) {
        for (WhoisListener listener : whoisListeners) {
            listener.whoisRealname(nick, realname);
        }
    }

    @Override
    public void whoisIdleTime(String nick, int seconds) {
        for (WhoisListener listener : whoisListeners) {
            listener.whoisIdleTime(nick, IrcUser.formatIdleTime(seconds));
        }
    }

    @Override
    public void channelListResponse(String name, String topic, String users) {
        int userCount = Integer.parseInt(users);
        channels.add(new SearchlistChannelItem(name, userCount, topic));
    }

    @Override
    public void serverDisconnected() {
        if (!disconnecting) {
            if (networkStateHandler.isConnected()) {
                startProtocolAdapter();
            }
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
            newHighlight(new IrcHighlight(ircChannel, ircMessage));
            if (getLastMessageChannels().contains((ircChannel.getChannelName()))) {
                readLastMessage(ircChannel.getChannelName());
            }
        } else if (!ircChannel.getChannelName().equals(activeChannel.getChannelName())) {
            ircMessage = connectedChannels.get(channel).newChatMessage(user, message);
            if (getLastMessageChannels().contains((ircChannel.getChannelName()))) {
                readLastMessage(ircChannel.getChannelName());
            }
            newLastMessage(new IrcHighlight(ircChannel, ircMessage));
        } else {
            ircMessage = connectedChannels.get(channel).newChatMessage(user, message);
        }

        for (SessionListener listener : sessionListeners) {
            listener.onChannelMessage(serverConnectionData.getServer(), channel, ircMessage);
        }
    }

    @Override
    public void queryMessageReceived(String user, String message) {
        user = IrcUser.extractUserName(user);
        boolean queryIsOpen = connectedChannels.containsKey(user);
        if (!queryIsOpen) {
            queryUser(user);
        }

        IrcChannel ircChannel = connectedChannels.get(user);
        ChatIrcMessage ircMessage = ircChannel.newChatMessage(user, message);
        if (!ircChannel.getChannelName().equals(activeChannel.getChannelName())) {
            if (getHighlightChannels().contains(ircChannel.getChannelName())) {
                readHighlight(ircChannel.getChannelName());
            }
            newHighlight(new IrcHighlight(ircChannel, ircMessage));
        }

        for (SessionListener listener : sessionListeners) {
            if (queryIsOpen) {
                listener.onChannelMessage(serverConnectionData.getServer(), user, ircMessage);
            }
        }
    }

    @Override
    public void ircError(String errorCode, String message) {
        String toastMessage = getStringByStringIdentifier("ERR_" + errorCode);
        switch (errorCode) {
            case IrcProtocolStrings.ERR_NOSUCHNICK:
                String user = message.split(" ")[1];
                queryError(toastMessage, user);
                break;
            case IrcProtocolStrings.ERR_NOSUCHSERVER:
                loginError(toastMessage);
                break;
            case IrcProtocolStrings.ERR_ERRONEUSNICKNAME:
            case IrcProtocolStrings.ERR_NICKNAMEINUSE:
                nickChangeError(toastMessage);
                break;
            case IrcProtocolStrings.ERR_USERONCHANNEL:
                inviteError(toastMessage);
                break;
            case IrcProtocolStrings.ERR_TOOMANYCHANNELS:
            case IrcProtocolStrings.ERR_CHANNELISFULL:
            case IrcProtocolStrings.ERR_INVITEONLYCHAN:
            case IrcProtocolStrings.ERR_BANNEDFROMCHAN:
                String channel = message.split(" ")[1];
                channelJoinError(toastMessage, channel);
                break;
        }
    }
    private String getStringByStringIdentifier(String name) {
        Context context = ContextHandler.CONTEXT;
        return context.getString(
                context.getResources().getIdentifier(name, "string", context.getPackageName()));
    }

    private void nickChangeError(String message) {
        if (connected) {
            for (SessionListener listener : sessionListeners) {
                listener.nickChangeError(message);
            }
        } else if (firstConnect) {
            String nick = user.getNick() + "_";
            serverConnectionData.setNickname(nick);
            user.changeNick(nick);
            connect();
        } else {
            loginError(message);
        }
    }

    private void queryError(String message, String user) {
        for (SessionListener listener : sessionListeners) {
            listener.queryError(message + " " + user);
        }
    }

    private void loginError(String message) {
        protocol.disconnect("");
        for (SessionListener listener : sessionListeners) {
            listener.loginError(message);
        }
    }

    private void channelJoinError(String message, String channel) {
        for (SessionListener listener : sessionListeners) {
            message = String.format(message, channel);
            listener.channelJoinError(message);
        }
    }

    private void inviteError(String message) {
        for (SessionListener listener : sessionListeners) {
            listener.inviteError(message);
        }
    }

    @Override
    public void onOnline() {
        startProtocolAdapter();
    }

    @Override
    public void onOffline() {
        connected = false;
    }
}
