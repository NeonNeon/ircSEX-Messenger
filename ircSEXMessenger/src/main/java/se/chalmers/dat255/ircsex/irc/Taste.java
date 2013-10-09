package se.chalmers.dat255.ircsex.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Created by oed on 10/9/13.
 */
public interface Taste {

    public BufferedReader getInput();

    public BufferedWriter getOutput();

    public void close();

}
