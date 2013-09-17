package se.chalmers.dat255.ircsex.irc;

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

    private BufferedReader input;
    private BufferedWriter output;

    private List<IrcProtocolServerListener> ircProtocolServerListeners;

    /**
     * Creates a socket connection to the specified server.
     *
     * @param host - the server to connect to
     * @param port - the port to use
     */
    public IrcProtocolAdapter(String host, int port) {
        createBuffers(host, port);
        ircProtocolServerListeners = new ArrayList<IrcProtocolServerListener>();
    }

    public void run() {
        String line = "";
        do {
            System.out.println(line);
            if (line.startsWith("PING ")) {
                write("PONG " + line.substring(5));
            }
            try {
                line = input.readLine();
                // TODO: Resolve nullpointerexception
            } catch (IOException e) {
                e.printStackTrace();
                propagateError(ErrorMessages.IOError);
            }
        } while(line != null);
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
    }

    private void createBuffers(String host, int port) {
        Socket socket;
        try {
            socket = new Socket(host, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            propagateError(ErrorMessages.IOError);
        }
    }

    private synchronized void write(String string) {
        System.out.println(string);
        try {
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            propagateError(ErrorMessages.IOError);
        }
    }

    private void propagateError(String errorMessage) {
        for (IrcProtocolServerListener listener : ircProtocolServerListeners) {
            listener.fireEvent(MessageType.ERROR, errorMessage);
        }
    }

    public void addIrcProtocolServerListener(IrcProtocolServerListener listener) {
        ircProtocolServerListeners.add(listener);
    }

    public void removeIrcProtocolServerListener(IrcProtocolServerListener listener) {
        ircProtocolServerListeners.remove(listener);
    }

    public enum MessageType {NORMAL, ERROR}

    public static class ErrorMessages {
        public static String IOError = "Socket disconnected";
    }

    /**
     * An interface that listens to events that are relevant to the IRC Server.
     */
    public interface IrcProtocolServerListener {
        public void fireEvent(MessageType type, String message);
    }

}
