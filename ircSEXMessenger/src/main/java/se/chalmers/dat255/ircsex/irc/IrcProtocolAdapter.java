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

    private boolean running = true;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private final String host;
    private final int port;

    private IrcProtocolListener listener;

    /**
     * Creates a socket connection to the specified server.
     *
     * @param host - the server to connect to
     * @param port - the port to use
     */
    public IrcProtocolAdapter(String host, int port, IrcProtocolListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    public void run() {
        createBuffers(host, port);
        String line = "";
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

    private void createBuffers(String host, int port) {
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
        //TODO - handle more cases
        System.out.println(reply);
        int index;
        if ((index = reply.indexOf("PRIVMSG")) != -1) {
            String nick = reply.substring(1, reply.indexOf('!'));
            int msgIndex = reply.indexOf(':', 1);
            String channel = reply.substring(index + 8, msgIndex - 1);
            String message = reply.substring(msgIndex + 1);

            if (channel.contains("#")) {
                listener.channelMessageReceived(channel, nick, message);
            }
            else {
                listener.queryMessageReceived(nick, message);
            }
        }
        else if (reply.startsWith("PING ")) {
            write("PONG " + reply.substring(5));
        }
        else if ((index = reply.indexOf("JOIN ")) != -1) {
            listener.userJoined(reply.substring(index + 6),
                    reply.substring(1, reply.indexOf('!')));
        }
        else if ((index = reply.indexOf("PART")) != -1) {
            listener.userParted(reply.substring(index + 5),
                    reply.substring(1, reply.indexOf('!')));
        }
        else if ((index = reply.indexOf("QUIT")) != -1) {
            String message = reply.substring(index + 6);
            String user = reply.substring(1, reply.indexOf('!'));
            listener.userQuit(user, message);
        }
        else if ((index = reply.indexOf("NICK ")) != -1) {
            listener.nickChanged(reply.substring(reply.indexOf(':') + 1, reply.indexOf('!')),
                    reply.substring(index + 6));
        }
        else if (reply.contains(":+wx")) { // TODO: This is hardcoded.
            listener.serverRegistered();
        }
        else if (reply.contains(host + " 353")) {
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
            System.out.println(nick+"|"+idleTime);
            listener.whoisIdleTime(nick, idleTime);
        }
        else if (reply.contains("322 ")) {
            if ((index = reply.indexOf("#")) != -1) {
                String channel = reply.substring(index, reply.indexOf(":", 1) - 1);
                String topic = reply.substring(reply.indexOf("] ") + 2);
                listener.channelListResponse(channel, topic);
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
        write("USER " + login + " 8 * : " + realName);
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
        write("QUIT :" + message);
        try {
            input.close();
            output.close();
            socket.close();
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
        write("JOIN " + channel);
    }

    /**
     * Join a channel that requires a key.
     * @param channel - the channel to join
     * @param key - the key to use
     */
    public void joinChannel(String channel, String key) {
        write("JOIN " + channel + " " + key);
    }

    /**
     * Part from a channel.
     * @param channel - the channel to part
     */
    public void partChannel(String channel) {
        write("PART " + channel);
    }


    /**
     * Set password for the IRC server.
     *
     * @param password - the password to use
     */
    private void setPassword(String password) {
        write("PASS " + password);
    }

    /**
     * Set or change the nickname.
     *
     * @param nick - the nick to use
     */
    public void setNick(String nick) {
        write("NICK " + nick);
    }

    /**
     * Sends a message to the server.
     * @param channel - the channel to send message to
     * @param message - the message to send.
     */
    public void sendChannelMessage(String channel, String message) {
        write("PRIVMSG " + channel + " :" + message);
    }

    /**
     * Send request to list the users in the given channel.
     * @param channel - the channel to check
     */
    public void getUsers(String channel) {
        write("NAMES " + channel);
    }

    /**
     * Send a request to get all channels on the server.
     */
    public void listChannels() {
        write("LIST");
    }

    /**
     * Send request to get whois info.
     * @param nick - the nick to get info for
     */
    public void whois(String nick) {
        write("WHOIS " + nick);
    }

    /**
     * Invite a user to a channel.
     * @param nick
     * @param channel
     */
    public void invite(String nick, String channel) {
        write("INVITE " + nick + " " + channel);
    }

    private synchronized void write(String string) {
        System.out.println(string);
        try {
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            listener.serverDisconnected();
        }
    }
}


