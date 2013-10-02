package se.chalmers.dat255.ircsex.ui;

import android.graphics.Rect;

/**
 * @author Johan Magnusson
 * Date: 2013-10-01
 */
public interface ChannelItem {

    /**
     * @return - the message to be displayed on the item
     */
    public String getMessage();

    /**
     * @return - the alignment of the item
     */
    public int getGravity();

    /**
     * @return - the padding in form of a rectangle, as the NinePatchDrawable class requires
     */
    public abstract Rect getPadding();

    /**
     * @return - the background overlay color
     */
    public abstract int getColor();

    /**
     * @return - ID of the drawable 9patch resource
     */
    public abstract int getNinePatchID();

    /**
     * @return ID of the layout xml resource
     */
    public int getLayoutID();
}
