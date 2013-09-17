package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oskar on 2013-09-17.
 */
public class Session {
    private List<IrcServer> servers;

    public Session() {
        servers = new ArrayList<IrcServer>();
    }

    public void addServer(String server, int port) {
        servers.add(new IrcServer(server, port));
    }

    public void removeServer(IrcServer server) {
        servers.remove(server);
    }
}
