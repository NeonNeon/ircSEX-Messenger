package se.chalmers.dat255.ircsex.ui;

import android.graphics.Rect;

/**
 * Created by Johan on 2013-09-24.
 */
public abstract class ChatBubble implements ChannelItem{
    private final String message;

    protected ChatBubble(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public abstract int getGravity();
    public abstract Rect getPadding();
    public abstract int getColor();
    public abstract int getNinePatchID();
    public abstract int getLayoutID();
    public abstract String getTimestamp();
}
