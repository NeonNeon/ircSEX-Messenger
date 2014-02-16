package se.chalmers.dat255.ircsex.ui;

import android.graphics.Rect;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChannelItem;
import se.chalmers.dat255.ircsex.model.IrcMessage;

/**
 * @author Johan Magnusson
 *         Date: 2013-10-10
 */
public class UnreadLine implements ChannelItem
{
    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public IrcMessage getIrcMessage() {
        return null;
    }

    @Override
    public int getGravity() {
        return 0;
    }

    @Override
    public Rect getPadding() {
        return null;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public int getNinePatchID() {
        return 0;
    }

    @Override
    public int getLayoutID() {
        return R.layout.unread_line;
    }
}
