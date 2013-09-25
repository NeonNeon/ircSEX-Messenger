package se.chalmers.dat255.ircsex.irc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private String host;
    private int port;

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
        if (reply.startsWith("PING ")) {
            write("PONG " + reply.substring(5));
        }
        else if ((index = reply.indexOf("JOIN")) != -1) {
            listener.userJoined(reply.substring(index + 6),
                    reply.substring(1, reply.indexOf('!')));
        }
        else if ((index = reply.indexOf("PART")) != -1) {
            listener.userParted(reply.substring(index + 5),
                    reply.substring(1, reply.indexOf('!')));
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
     * Set or change the nickname.
     *
     * @param nick - the nick to use
     */
    public void setNick(String nick) {
        write("NICK " + nick);
    }

    /**
     * Send request to list the users in the given channel.
     * @param channel - the channel to check
     */
    public void getUsers(String channel) {
        write("NAMES " + channel);
    }

    public void whois(String nick) {
        write("WHOIS " + nick);
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


