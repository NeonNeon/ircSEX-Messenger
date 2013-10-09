package se.chalmers.dat255.ircsex.model;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChannelItem;
import se.chalmers.dat255.ircsex.model.IrcMessage;

/**
 * Created by Johan on 2013-10-01.
 */
public class InfoMessage implements ChannelItem {
    private final InfoIrcMessage ircMessage;

    public InfoMessage(IrcMessage ircMessage) {
        this.ircMessage = (InfoIrcMessage) ircMessage;
    }

    @Override
    public String getMessage() {
        return ircMessage.getMessage();
    }

    @Override
    public IrcMessage getIrcMessage() {
        return ircMessage;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public Rect getPadding() {
        return new Rect(40, 15, 40, 20);
    }

    @Override
    public int getColor() {
        return Color.rgb(240, 240, 240);
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.message_item;
    }

    @Override
    public int getLayoutID() {
        return R.layout.info_message;
    }
}
