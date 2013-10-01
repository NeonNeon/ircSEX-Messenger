package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-10-01.
 */
public class InfoMessage implements ChannelItem {
    private final String message;

    public InfoMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public Rect getPadding() {
        return new Rect(30, 30, 30, 30);
    }

    @Override
    public int getColor() {
        return Color.rgb(195, 195, 195);
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.right_chat_bubble;
    }

    @Override
    public int getLayoutID() {
        return R.layout.channel_message;
    }
}
