package se.chalmers.dat255.ircsex.model;

import se.chalmers.dat255.ircsex.ui.ChannelItem;

/**
 * Created by Wilhelm on 2013-10-09.
 */
public class IrcMessage {
    protected final String message;
    private boolean read = false;

    public IrcMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if the message is read.
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

    /**
     * Get the message
     * @return
     */
    public String getMessage() {
        return message;
    }

    public Class<? extends ChannelItem> getChannelItem() { return null; }
}
