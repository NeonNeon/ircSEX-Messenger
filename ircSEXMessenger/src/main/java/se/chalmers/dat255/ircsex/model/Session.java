package se.chalmers.dat255.ircsex.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oskar on 2013-09-17.
 */
public class Session {
    private Map<String, IrcServer> servers;

    public Session() {
        servers = new HashMap<String, IrcServer>();
    }

    public void addServer(String server, int port, String nick) {
        servers.put(server, new IrcServer(server, port, nick));
    }

    public void removeServer(String host) {
        servers.remove(host);
    }
}
