package se.chalmers.dat255.ircsex.irc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

/**
 * This class is used to easily communicate with the IRC protocol.
 *
 * Created by oed on 9/16/13.
 */
public class IrcProtocolAdapter implements Runnable {

    private static final String BLANK = " ";
    private static final String COLON = ":";
    private static final String HASHTAG = "#";
    private static final String BANG = "!";

    private boolean running = true;
    private Taste taste;
    private BufferedReader input;
    private BufferedWriter output;

    private IrcProtocolListener listener;

    /**
     * Creates a socket connection to the specified server.
     *
     * @param taste - the taste of the IPA
     * @param listener - the listener to use
     */
    public IrcProtocolAdapter(Taste taste, IrcProtocolListener listener) {
        this.taste = taste;
        this.listener = listener;
    }

    public void run() {
        createBuffers();
        String line = "   ";
        do {
            Log.e("IRC", line);
            handleReply(line);
            try {
                line = input.readLine();
                // TODO: Resolve nullpointerexception
            } catch (IOException e) {
                e.printStackTrace();
                listener.serverDisconnected();
            }
        } while(running && line != null);
    }

    private void createBuffers() {
        try {
            output = taste.getOutput();
            input = taste.getInput();
        } catch (IOException e) {
            e.printStackTrace();
            listener.serverDisconnected();
        }
        listener.serverConnected();
    }

    /**
     * This method parses a reply and propagates either
     * a message or a channel message.
     */
    private void handleReply(String reply) {
        System.out.println(reply);

        String[] parts = reply.split(BLANK, 3);

        handlePing(parts);

        switch (parts[1]) {
            case IrcProtocolStrings.PRIVMSG:
                handlePrivmsg(parts);
                break;
            case IrcProtocolStrings.JOIN:
                listener.userJoined(
                        parts[2].substring(parts[2].indexOf(COLON) + 1),
                        parts[0].substring(1, parts[0].indexOf(BANG)));
                break;
            case IrcProtocolStrings.PART:
                listener.userParted(
                        parts[2],
                        parts[0].substring(1, parts[0].indexOf(BANG)));
                break;
            case IrcProtocolStrings.QUIT:
                listener.userQuit(
                        parts[0].substring(1, parts[0].indexOf(BANG)),
                        parts[2].substring(parts[2].indexOf(COLON) + 1));
                break;
            case IrcProtocolStrings.NICK:
                listener.nickChanged(
                        parts[0].substring(1, parts[0].indexOf(BANG)),
                        parts[2].substring(parts[2].indexOf(COLON) + 1));
                break;
            case IrcProtocolStrings.RPL_WELCOME:
                listener.serverRegistered(
                        parts[0].substring(1),
                        parts[2].substring(0, parts[2].indexOf(BLANK)));
                break;
        }

        handleReplyOld(reply);
    }

    private void handlePing(String[] parts) {
        if (parts[0].equals(IrcProtocolStrings.PING)) {
            write(IrcProtocolStrings.PONG + BLANK + parts[1]);
        }
    }

    private void handlePrivmsg(String[] parts) {
        String nick = parts[0].substring(1, parts[0].indexOf(BANG));
        String channel = parts[2].substring(0, parts[2].indexOf(BLANK));
        String msg = parts[2].substring(parts[2].indexOf(COLON) + 1);
        if (channel.contains(HASHTAG)) {
            listener.channelMessageReceived(channel, nick, msg);
        } else {
            listener.queryMessageReceived(nick, msg);
        }
    }

    private void handleReplyOld(String reply) {
        //TODO - handle more cases
        int index;
        if (reply.contains(" 353")) {
            index = reply.indexOf("=");
            String channel = reply.substring(index + 2, reply.indexOf(" ", index + 2));

            index = reply.indexOf(':', 1);

            listener.usersInChannel(channel, Arrays.asList(reply.substring(index + 1).split(" ")));
        }
        else if ((index = reply.indexOf("311 ")) != -1) {
            int index2 = reply.indexOf(' ', index + 5) + 1;
            String nick = reply.substring(index2, reply.indexOf(' ', index2));
            String realname = reply.substring(reply.lastIndexOf(':') + 1);
            listener.whoisRealname(nick, realname);
        }
        else if ((index = reply.indexOf("319 ")) != -1) {
            int index2 = reply.indexOf(' ', index + 5) + 1;
            String nick = reply.substring(index2, reply.indexOf(' ', index2));
            String channels = reply.substring(reply.lastIndexOf(':') + 1);
            listener.whoisChannels(nick, Arrays.asList(channels.split(" ")));
        }
        else if ((index = reply.indexOf("317 ")) != -1) {
            int index2 = reply.indexOf(' ', index + 5) + 1;
            int index3 = reply.indexOf(' ', index2);
            String nick = reply.substring(index2, index3);
            int idleTime = Integer.parseInt(reply.substring(index3 + 1, reply.indexOf(' ', index3 + 1)));
            listener.whoisIdleTime(nick, idleTime);
        }
        else if (reply.contains("322 ")) {
            if ((index = reply.indexOf("#")) != -1) {
                String channel = reply.substring(index, reply.indexOf(":", 1) - 1);
                String topic = reply.substring(reply.indexOf("] ") + 2);
                String users = channel.substring(channel.indexOf(" ") + 1);
                channel = channel.substring(0, channel.indexOf(" "));
                listener.channelListResponse(channel, topic, users);
            }
        }

        // Numeric replies - should be after everything else
        // Should maybe be implemented safer.
        else if (reply.contains("433")) {
            listener.nickChangeError();
        }
    }

    /**
     * Connect to the specified server with this identity.
     *
     * @param nick - the nick to use
     * @param login - the login to use
     * @param realName - the users realname
     */
    public void connect(String nick, String login, String realName) {
        setNick(nick);
        if (login.isEmpty()) {
            login = nick;
        }
        write(IrcProtocolStrings.USER + BLANK + login + " 8 * : " + realName);
    }

    /**
     * Connect to the specified server with this identity and password.
     *
     * @param nick - the nick to use
     * @param login - the login to use
     * @param realName - the users realname
     * @param password - the password to use
     */
    public void connect(String nick, String login, String realName, String password){
        connect(nick, login, realName);
        setPassword(password);
    }

    /**
     * Disconnect from the current server.
     *
     * @param message - the message to be displayed when quiting
     */
    public void disconnect(String message) {
        write(IrcProtocolStrings.QUIT + BLANK + COLON + message);
        try {
            taste.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            running = false;
        }

    }

    /**
     * Join a channel.
     * @param channel - the channel to join
     */
    public void joinChannel(String channel) {
        write(IrcProtocolStrings.JOIN + BLANK + channel);
    }

    /**
     * Join a channel that requires a key.
     * @param channel - the channel to join
     * @param key - the key to use
     */
    public void joinChannel(String channel, String key) {
        write(IrcProtocolStrings.JOIN + BLANK + channel + BLANK + key);
    }

    /**
     * Part from a channel.
     * @param channel - the channel to part
     */
    public void partChannel(String channel) {
        write(IrcProtocolStrings.PART + BLANK + channel);
    }


    /**
     * Set password for the IRC server.
     *
     * @param password - the password to use
     */
    private void setPassword(String password) {
        write(IrcProtocolStrings.PASS + BLANK + password);
    }

    /**
     * Set or change the nickname.
     *
     * @param nick - the nick to use
     */
    public void setNick(String nick) {
        write(IrcProtocolStrings.NICK + BLANK + nick);
    }

    /**
     * Sends a message to the server.
     * @param channel - the channel to send message to
     * @param message - the message to send.
     */
    public void sendChannelMessage(String channel, String message) {
        write(IrcProtocolStrings.PRIVMSG + BLANK + channel + BLANK + COLON + message);
    }

    /**
     * Send request to list the users in the given channel.
     * @param channel - the channel to check
     */
    public void getUsers(String channel) {
        write(IrcProtocolStrings.NAMES + BLANK + channel);
    }

    /**
     * Send a request to get all channels on the server.
     */
    public void listChannels() {
        write(IrcProtocolStrings.LIST);
    }

    /**
     * Send request to get whois info.
     * @param nick - the nick to get info for
     */
    public void whois(String nick) {
        write(IrcProtocolStrings.WHOIS + BLANK + nick);
    }

    /**
     * Invite a user to a channel.
     * @param nick
     * @param channel
     */
    public void invite(String nick, String channel) {
        write(IrcProtocolStrings.INVITE + BLANK + nick + BLANK + channel);
    }

    private synchronized void write(String string) {
        System.out.print(string);
        try {
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            listener.serverDisconnected();
        }
    }
}


