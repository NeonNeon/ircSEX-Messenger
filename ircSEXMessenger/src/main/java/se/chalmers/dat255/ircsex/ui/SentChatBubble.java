package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-24.
 */
public class SentChatBubble extends ChatBubble {
    public SentChatBubble(String message) {
        super(message);
    }

    @Override
    public int getGravity() {
        return Gravity.RIGHT;
    }

    @Override
    public int getBackgroundColor() {
        return Color.GREEN;
    }

    @Override
    public int getLayoutID() {
        return R.layout.sent_chat_bubble;
    }
}
