package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-09-24.
 */
public class IrcUser implements Comparable<IrcUser> {

    private String nick;
    private char status;
    private boolean owner;
    private boolean op;
    private boolean halfOp;
    private boolean voice;

    public static final char OWNER = '~';
    public static final char OP = '@';
    public static final char HALF_OP = '%';
    public static final char VOICE = '+';
    public static final char NO_STATUS = (char) -1;

    public IrcUser(String user, char status) {
        this.nick = user;
        this.status = status;
        owner = status == OWNER;
        op = status == OP;
        voice = status == VOICE;
        halfOp = status == HALF_OP;
    }

    /**
     * Returns the nickname.
     *
     * @return The nickname
     */
    public String getNick() {
        return nick;
    }

    public boolean isOwner() {
        return owner;
    }

    public boolean isOp() {
        return op;
    }

    public boolean isHalfOp() {
        return halfOp;
    }

    public boolean isVoice() {
        return voice;
    }

    public void changeNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String toString() {
        return (status == NO_STATUS ? "" : status) + nick;
    }

    @Override
    public int compareTo(IrcUser ircUser) {
        if (owner != ircUser.isOwner()) {
            return Boolean.valueOf(ircUser.isOwner()).compareTo(Boolean.valueOf(owner));
        } else if (op != ircUser.isOp()) {
            return Boolean.valueOf(ircUser.isOp()).compareTo(Boolean.valueOf(op));
        } else if (halfOp != ircUser.isHalfOp()) {
            return Boolean.valueOf(ircUser.isHalfOp()).compareTo(Boolean.valueOf(halfOp));
        } else if (voice != ircUser.isVoice()) {
            return Boolean.valueOf(ircUser.isVoice()).compareTo(Boolean.valueOf(voice));
        } else {
            return nick.toLowerCase().compareTo(ircUser.getNick().toLowerCase());
        }
    }

    /**
     * Extracts the status sign in a username.
     *
     * @param username Username to extract status sign from
     * @return Status sign
     */
    public static char extractUserStatus(String username) {
        char first = username.charAt(0);
        if (first == OP || first == VOICE || first == OWNER || first == HALF_OP) {
            return first;
        } else {
            return NO_STATUS;
        }
    }

    /**
     * Extracts the username from the string status+username.
     *
     * @param username Username to extract from
     * @return Extracted username
     */
    public static String extractUserName(String username) {
        char status = extractUserStatus(username);
        if (status != NO_STATUS) {
            return username.substring(1);
        } else {
            return username;
        }
    }
}
