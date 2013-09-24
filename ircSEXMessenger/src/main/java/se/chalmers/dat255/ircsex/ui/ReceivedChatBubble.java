package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-24.
 */
public class ReceivedChatBubble extends ChatBubble {
    private String nick;

    protected ReceivedChatBubble(String nick, String message) {
        super(message);
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public int getGravity() {
        return Gravity.LEFT;
    }

    @Override
    public int getBackgroundColor() {
        return Color.GRAY;
    }

    @Override
    public int getLayoutID() {
        return R.layout.received_chat_bubble;
    }
}
