package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-10-10.
 */
public class IrcHighlight {

    private final IrcChannel channel;
    private final IrcMessage message;
    private boolean read;

    public IrcHighlight(IrcChannel channel, IrcMessage message) {
        this.channel = channel;
        this.message = message;
        read = false;
    }

    public IrcMessage getMessage() {
        return message;
    }

    public IrcChannel getChannel() {
        return channel;
    }

    public void read() {
        read = true;
    }

    public boolean isRead() {
        return read;
    }
}
