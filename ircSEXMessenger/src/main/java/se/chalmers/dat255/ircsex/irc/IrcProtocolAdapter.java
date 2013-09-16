package se.chalmers.dat255.ircsex.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by oed on 9/16/13.
 */
public class IrcProtocolAdapter implements Runnable {

    private BufferedReader input;
    private BufferedWriter output;

    public IrcProtocolAdapter(String server, int port) throws IOException {
        createBuffers(server, port);
    }

    public void run() {
        String line = null;
        do {
            try {
                line = input.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO - send event
            }
            System.out.println(line);
            if (line.startsWith("PING ")) {
                write("PONG " + line.substring(5));
            }
        } while(true);
    }

    public void  connect(String nick, String login, String realName) {
        write("NICK " + nick);
        write("USER " + login + " 8 * : " + realName);
    }

    private void createBuffers(String server, int port) throws IOException {
        Socket socket = new Socket(server, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private synchronized void write(String string) {
        System.out.println(string);
        try {
            output.write(string + "\r\n");
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //TODO - send event
        }
    }
}
