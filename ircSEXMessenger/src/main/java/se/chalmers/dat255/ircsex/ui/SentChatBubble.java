package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;

import java.text.SimpleDateFormat;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-24.
 */
public class SentChatBubble extends ChatBubble {
    private final long timestamp;

    public SentChatBubble(String message) {
        super(message);
        timestamp = System.currentTimeMillis();
    }

    @Override
    public int getGravity() {
        return Gravity.RIGHT;
    }

    @Override
    public Rect getPadding() {
        return new Rect(30, 20, 40, 30);
    }

    @Override
    public int getColor() {
        return Color.rgb(195, 229, 183);
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.right_chat_bubble;
    }

    @Override
    public int getLayoutID() {
        return R.layout.sent_chat_bubble;
    }

    @Override
    public String getTimestamp() {
        if (System.currentTimeMillis()/1000L - timestamp < 60*60*24) {
            return new SimpleDateFormat("HH:mm").format(timestamp);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        }
    }
}