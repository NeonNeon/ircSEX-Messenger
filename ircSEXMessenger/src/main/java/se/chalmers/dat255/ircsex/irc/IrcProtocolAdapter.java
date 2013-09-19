package se.chalmers.dat255.ircsex.irc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
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

    private List<IrcProtocolServerListener> ircProtocolServerListeners;

    /**
     * Creates a socket connection to the specified server.
     *
     * @param host - the server to connect to
     * @param port - the port to use
     */
    public IrcProtocolAdapter(String host, int port) {
        this.host = host;
        this.port = port;
        ircProtocolServerListeners = new ArrayList<IrcProtocolServerListener>();
    }

    public void run() {
        createBuffers(host, port);
        String line = "";
        do {
            handleReply(line);
            Log.e("IRC", line);
            try {
                line = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                propagateMessage(MessageType.ERROR, ErrorMessages.IOError);
            }
        } while(running && line != null);
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
            propagateMessage(MessageType.JOIN, reply.substring(index + 6));
        }
        else if ((index = reply.indexOf("PART")) != -1) {
            propagateMessage(MessageType.PART, reply.substring(index + 5));
        }
        else if (reply.contains("MODE")) {
            propagateMessage(MessageType.SERVER_REGISTERED, null);
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
        write("NICK " + nick);
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

    private void createBuffers(String host, int port) {
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            propagateMessage(MessageType.ERROR, ErrorMessages.IOError);
        }
        propagateMessage(MessageType.NORMAL, Messages.IOConnected);
    }

    private synchronized void write(String string) {
        System.out.println(string);
        try {
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            propagateMessage(MessageType.ERROR, ErrorMessages.IOError);
        }
    }

    private void propagateChannelMessage(MessageType type, String channel, String message) {
        for (IrcProtocolServerListener listener : ircProtocolServerListeners) {
            listener.fireChannelEvent(type, channel, message);
        }
    }

    private void propagateMessage(MessageType type, String message) {
        for (IrcProtocolServerListener listener : ircProtocolServerListeners) {
            listener.fireEvent(type, message);
        }
    }
    public void addIrcProtocolServerListener(IrcProtocolServerListener listener) {
        ircProtocolServerListeners.add(listener);
    }

    public void removeIrcProtocolServerListener(IrcProtocolServerListener listener) {
        ircProtocolServerListeners.remove(listener);
    }

    public enum MessageType {NORMAL, ERROR, SERVER_REGISTERED, JOIN, PART}

    public static class ErrorMessages {
        public static final String IOError = "Socket disconnected";
    }

    public static class Messages {
        public static final String IOConnected = "Socket created";
    }

    /**
     * An interface that listens to events that are relevant to the IRC Server.
     */
    public interface IrcProtocolServerListener {
        public void fireEvent(MessageType type, String message);
        public void fireChannelEvent(MessageType type, String channel, String message);
    }

}
