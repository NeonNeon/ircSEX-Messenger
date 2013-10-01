package se.chalmers.dat255.ircsex.ui;

import android.graphics.Rect;

/**
 * Created by Johan on 2013-10-01.
 */
public interface ChannelItem {
    public String getMessage();
    public int getGravity();
    public abstract Rect getPadding();
    public abstract int getColor();
    public abstract int getNinePatchID();
    public int getLayoutID();
}
