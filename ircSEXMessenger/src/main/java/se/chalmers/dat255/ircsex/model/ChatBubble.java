package se.chalmers.dat255.ircsex.model;

import android.graphics.Rect;

/**
 * Created by Johan on 2013-09-24.
 */
public abstract class ChatBubble implements ChannelItem {
    private final IrcMessage ircMessage;

    protected ChatBubble(IrcMessage ircMessage) {
        this.ircMessage = ircMessage;
    }

    public String getMessage() {
        return ircMessage.getMessage();
    }

    @Override
    public IrcMessage getIrcMessage() {
        return ircMessage;
    }

    public abstract int getGravity();
    public abstract Rect getPadding();
    public abstract int getColor();
    public abstract int getNinePatchID();
    public abstract int getLayoutID();
    public abstract String getTimestamp();
}
