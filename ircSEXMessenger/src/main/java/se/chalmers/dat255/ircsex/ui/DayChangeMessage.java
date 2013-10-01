package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-10-01.
 */
public class DayChangeMessage implements ChannelItem {
    private final String message;

    public DayChangeMessage(String message) {
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
        return new Rect(40, 15, 40, 20);
    }

    @Override
    public int getColor() {
        return Color.rgb(240, 240, 240);
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.left_chat_bubble;
    }

    @Override
    public int getLayoutID() {
        return R.layout.day_change_message;
    }
}
