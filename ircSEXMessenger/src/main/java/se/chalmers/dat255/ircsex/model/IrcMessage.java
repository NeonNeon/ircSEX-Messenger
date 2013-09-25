package se.chalmers.dat255.ircsex.model;

/**
 * Class to represent an message.
 *
 * Created by Oskar on 2013-09-24.
 */
public class IrcMessage {

    private final String user;
    private final String message;
    private final long timestamp;
    private boolean read;

    public IrcMessage(String user, String message, long timestamp) {
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
        this.read = false;
    }

    /**
     * Checks if the message is read.
     *
     * @return if the message is read
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Marks message as read.
     */
    public void read() {
        read = true;
    }
}
