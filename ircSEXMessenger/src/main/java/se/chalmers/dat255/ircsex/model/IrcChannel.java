package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;

/**
 * Created by Oskar on 2013-09-17.
 */
public class IrcChannel {

    private final String channelName;
    private final List<String> users;

    public IrcChannel(String channelName, IrcProtocolAdapter protocol) {
        this.channelName = channelName;
        users = new ArrayList<String>();
    }
}
