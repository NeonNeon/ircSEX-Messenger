package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.graphics.Rect;
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
    public Rect getPadding() {
        return new Rect(40, 20, 30, 30);
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
}
