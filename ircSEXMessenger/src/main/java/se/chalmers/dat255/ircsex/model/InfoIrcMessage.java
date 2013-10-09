package se.chalmers.dat255.ircsex.model;

import se.chalmers.dat255.ircsex.ui.ChannelItem;
import se.chalmers.dat255.ircsex.ui.InfoMessage;

/**
 * Created by Wilhelm on 2013-10-09.
 */
public class InfoIrcMessage extends IrcMessage {
    public InfoIrcMessage(String message) {
        super(message);
    }

    @Override
    public Class<? extends ChannelItem> getChannelItem() {
        return InfoMessage.class;
    }
}
