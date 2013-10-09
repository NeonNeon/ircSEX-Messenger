package se.chalmers.dat255.ircsex.model;

import java.text.SimpleDateFormat;

import se.chalmers.dat255.ircsex.ui.ChannelItem;
import se.chalmers.dat255.ircsex.ui.ReceivedChatBubble;
import se.chalmers.dat255.ircsex.ui.SentChatBubble;

/**
 * Class to represent an message.
 *
 * Created by Oskar on 2013-09-24.
 */
public class ChatIrcMessage extends IrcMessage {
    private static final int ONE_DAY = 60 * 60 * 24;
    private static final long MS_MULTIPLIER = 1000L;
    private final IrcUser user;
    private final long timestamp;

    public ChatIrcMessage(IrcUser user, String message) {
        super(message);
        this.user = user;
        this.timestamp = System.currentTimeMillis();
    }

    public IrcUser getUser() {
        return user;
    }

    public String getReadableTimestamp() {
        if (System.currentTimeMillis()/ MS_MULTIPLIER - timestamp < ONE_DAY) {
            return new SimpleDateFormat("HH:mm").format(timestamp);
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        }
    }

    @Override
    public Class<? extends ChannelItem> getChannelItem() {
        return user.isSelf() ? SentChatBubble.class : ReceivedChatBubble.class;
    }
}
