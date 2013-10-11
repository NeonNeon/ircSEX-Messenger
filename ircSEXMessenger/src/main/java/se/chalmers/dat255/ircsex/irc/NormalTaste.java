package se.chalmers.dat255.ircsex.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by oed on 10/9/13.
 */
public class NormalTaste implements Taste {

    private String host;
    private int port;
    private boolean socketCreated;

    private Socket socket;

    public NormalTaste(String host, int port) {
        this.host = host;
        this.port = port;
        socketCreated = false;
    }

    @Override
    public BufferedReader getInput() throws IOException {
        checkSocket();
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public BufferedWriter getOutput() throws IOException {
        checkSocket();
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private void checkSocket() throws IOException {
        if (!socketCreated) {
            createSocket();
            socketCreated = true;
        }
    }

    private void createSocket() throws IOException {
        socket = new Socket(host, port);
    }
}
