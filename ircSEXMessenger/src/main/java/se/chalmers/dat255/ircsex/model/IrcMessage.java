package se.chalmers.dat255.ircsex.model;

/**
 * Created by Wilhelm on 2013-10-09.
 */
public abstract class IrcMessage {
    protected final String message;
    private boolean read = false;
    private boolean highlight = false;

    public IrcMessage(String message) {
        this.message = message;
    }

    public IrcMessage(String message, boolean highlight) {
        this(message);
        this.highlight = highlight;
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

    /**
     * Returns whether the message i a highlight.
     * @return
     */
    public boolean isHighlight() {
        return highlight;
    }

    public abstract Class<? extends ChannelItem> getChannelItem();
}
