package se.chalmers.dat255.ircsex.model;

import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-24.
 */
public class SentChatBubble extends ChatBubble {
    private final ChatIrcMessage ircMessage;

    public SentChatBubble(IrcMessage ircMessage) {
        super(ircMessage);
        this.ircMessage = (ChatIrcMessage) ircMessage;
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
        return Color.parseColor("#FFF6F6F6");
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
        return ircMessage.getReadableTimestamp();
    }
}
