package se.chalmers.dat255.ircsex.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Created by oed on 10/9/13.
 */
public class NormalTaste implements Taste {

    private String host;
    private int port;

    public NormalTaste(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public BufferedReader getInput() {
        return null;
    }

    @Override
    public BufferedWriter getOutput() {
        return null;
    }

    @Override
    public void close() {

    }
}
