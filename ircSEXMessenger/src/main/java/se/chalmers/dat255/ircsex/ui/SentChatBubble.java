package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.view.Gravity;

/**
 * Created by Johan on 2013-09-24.
 */
public class SentChatBubble extends ChatBubble {
    public SentChatBubble(String nick, String message) {
        super(nick, message);
    }

    @Override
    public int getGravity() {
        return Gravity.RIGHT;
    }

    @Override
    public int getBackgroundColor() {
        return Color.GREEN;
    }
}
