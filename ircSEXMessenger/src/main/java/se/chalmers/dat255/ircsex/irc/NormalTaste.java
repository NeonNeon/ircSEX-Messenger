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
    private boolean buffersCreated;

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    public NormalTaste(String host, int port) {
        this.host = host;
        this.port = port;
        buffersCreated = false;
    }

    @Override
    public BufferedReader getInput() throws IOException {
        checkBuffers();
        return input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public BufferedWriter getOutput() throws IOException {
        checkBuffers();
        return output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    private void checkBuffers() throws IOException {
        if (!buffersCreated) {
            createBuffers();
            buffersCreated = true;
        }
    }

    private void createBuffers() throws IOException {
        socket = new Socket(host, port);
    }
}
