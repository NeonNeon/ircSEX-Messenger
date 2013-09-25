package se.chalmers.dat255.ircsex.ui;

/**
 * Created by Johan on 2013-09-24.
 */
public abstract class ChatBubble {
    private final String nick;
    private final String message;

    protected ChatBubble(String nick, String message) {
        this.nick = nick;
        this.message = message;
    }

    public String getNick() {
        return nick;
    }

    public String getMessage() {
        return message;
    }

    public abstract int getGravity();
    public abstract int getBackgroundColor();
}
