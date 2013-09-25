package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.view.Gravity;

/**
 * Created by Johan on 2013-09-24.
 */
public class RecievedChatBubble extends ChatBubble {

    protected RecievedChatBubble(String nick, String message) {
        super(nick, message);
    }

    @Override
    public int getGravity() {
        return Gravity.LEFT;
    }

    @Override
    public int getBackgroundColor() {
        return Color.GRAY;
    }
}
