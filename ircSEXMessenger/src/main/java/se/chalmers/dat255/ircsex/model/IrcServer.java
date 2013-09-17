package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer {

    private final String host;
    private final int port;

    private final IrcProtocolAdapter protocol;
    private final List<IrcChannel> channels;

    /**
     * Initializes variables and creates a IrcProtocolAdapter.
     *
     * @param host - the server to connect to
     * @param port - the port to use
     */
    public IrcServer(String host, int port) {
        this.host = host;
        this.port = port;

        channels = new ArrayList<IrcChannel>();
        protocol = new IrcProtocolAdapter(host, port);
    }
}
