package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-10-10.
 */
public class IrcHighlight {

    private final IrcChannel channel;
    private final IrcMessage message;

    public IrcHighlight(IrcChannel channel, IrcMessage message) {
        this.channel = channel;
        this.message = message;
    }

    public IrcMessage getMessage() {
        return message;
    }

    public IrcChannel getChannel() {
        return channel;
    }
}
