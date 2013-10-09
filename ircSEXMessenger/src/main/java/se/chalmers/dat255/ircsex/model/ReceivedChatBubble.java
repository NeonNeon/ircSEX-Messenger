package se.chalmers.dat255.ircsex.model;

import android.graphics.Rect;
import android.view.Gravity;

import se.chalmers.dat255.ircsex.R;
import se.chalmers.dat255.ircsex.model.ChatBubble;
import se.chalmers.dat255.ircsex.model.ChatIrcMessage;
import se.chalmers.dat255.ircsex.model.IrcMessage;
import se.chalmers.dat255.ircsex.model.IrcUser;

/**
 * Created by Johan on 2013-09-24.
 */
public class ReceivedChatBubble extends ChatBubble {
    private final IrcUser ircUser;
    private final ChatIrcMessage ircMessage;

    public ReceivedChatBubble(IrcMessage ircMessage) {
        super(ircMessage);
        this.ircMessage = (ChatIrcMessage) ircMessage;
        this.ircUser = this.ircMessage.getUser();
    }

    public String getNick() {
        return ircUser.getNick();
    }

    @Override
    public int getGravity() {
        return Gravity.LEFT;
    }

    @Override
    public Rect getPadding() {
        return new Rect(40, 20, 30, 30);
    }

    @Override
    public int getColor() {
        return ircUser.getColor();
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.left_chat_bubble;
    }

    @Override
    public int getLayoutID() {
        return R.layout.received_chat_bubble;
    }

    @Override
    public String getTimestamp() {
        return ircMessage.getReadableTimestamp();
    }
}
