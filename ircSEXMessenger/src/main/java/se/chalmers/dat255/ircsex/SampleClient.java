package se.chalmers.dat255.ircsex;

import java.io.*;
import java.net.*;

public class SampleClient {
    private static BufferedWriter writer;
    private static BufferedReader reader;

    public static void main(String[] args) throws Exception {
        String server = "irc.chalmers.it";
        String nick = "tord";
        String login = "tord";
        String channel = "#ircSEX-asp";

        Socket socket = new Socket(server, 6667);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        write("NICK " + nick);
        write("USER " + login + " 8 * : Test test");
        writer.flush();

        String line;
        while ((line = reader.readLine( )) != null) {
            System.out.println(line);
            if (line.startsWith("PING ")) {
                write("PONG " + line.substring(5));
                writer.flush();
                break;
            }
        }

        write("JOIN " + channel);
        write("PRIVMSG " + channel + " :push it to the limit!");
        writer.flush();

        writer.close();
        reader.close();
        socket.close();
    }

    private static void write(String string) throws IOException {
        System.out.println(string);
        writer.write(string + "\r\n");
    }
}