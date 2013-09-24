package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-09-24.
 */
public class IrcMessage {

    private final String user;
    private final String message;
    private final Long timestamp;

    public IrcMessage(String user, String message, Long timestamp) {
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }
}
