package se.chalmers.dat255.ircsex.model;

import java.util.Calendar;
import java.util.Date;

import se.chalmers.dat255.ircsex.SampleSSH;

/**
 * Created by oed on 2/16/14.
 */
public class IrcMessage {

    private final String message;
    private final String owner;
    private final Date timestamp;
    private final boolean isHilight;

    public IrcMessage(String message, String owner, Date timestamp, boolean isHilight) {
        this.message = message;
        this.owner = owner;
        this.timestamp = timestamp;
        this.isHilight = isHilight;
    }

    public String getMessage() {
        return message;
    }

    public String getOwner() {
        return owner;
    }

    public String getFormattedTimestamp() {
        return timestamp.toString();
    }

    protected Date getTimestamp() {
        return timestamp;
    }

    public boolean isHilight() {
        return isHilight;
    }

}
