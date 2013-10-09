package se.chalmers.dat255.ircsex.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by oed on 10/9/13.
 */
public interface Taste {

    public BufferedReader getInput() throws IOException;

    public BufferedWriter getOutput() throws IOException;

    public void close() throws IOException;

}
