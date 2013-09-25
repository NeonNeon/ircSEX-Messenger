package se.chalmers.dat255.ircsex.model;

import java.text.SimpleDateFormat;

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

    public IrcMessage(String user, String message) {
        this.user = user;
        this.message = message;
        this.timestamp = System.currentTimeMillis()/1000L;
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

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public String getReadableTimestamp() {
        if (System.currentTimeMillis()/1000L - timestamp < 60*60*24) {
            return new SimpleDateFormat("HH:mm").format(timestamp);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        }
    }
}
