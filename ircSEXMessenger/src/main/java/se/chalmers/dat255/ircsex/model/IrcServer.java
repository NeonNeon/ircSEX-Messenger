package se.chalmers.dat255.ircsex.model;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;

/**
 * This class lists and handles a server, including the protocol adapter and channels.
 *
 * Created by Oskar on 2013-09-17.
 */
public class IrcServer implements IrcProtocolAdapter.IrcProtocolServerListener {

    private final String host;
    private final int port;
    private final String login;
    private final String nick;
    private final String realName;

    private IrcProtocolAdapter protocol;

    private final List<IrcChannel> channels;
    private final List<IrcChannel> connectedChannels;

    public IrcServer(String host, int port, String nick) {
        this(host, port, nick, nick);
    }

    public IrcServer(String host, int port, String login, String nick) {
        this(host, port, login, nick, "");
    }

    public IrcServer(String host, int port, String login, String nick, String realName) {
        this.host = host;
        this.port = port;
        this.login = login;
        this.nick = nick;
        this.realName = realName;

        channels = new ArrayList<IrcChannel>();
        connectedChannels = new ArrayList<IrcChannel>();

        startProtocolAdapter(host, port, nick, login, realName);
    }

    public void startProtocolAdapter(String host, int port, String nick, String login, String realName) {
        protocol = new IrcProtocolAdapter(host, port);
        protocol.addIrcProtocolServerListener(this);
        new Thread(protocol).start();
    }

    @Override
    public void fireEvent(IrcProtocolAdapter.MessageType type, String message) {
        if (message == IrcProtocolAdapter.Messages.IOConnected) {
            protocol.connect(nick, login, realName);
        }
    }
}
