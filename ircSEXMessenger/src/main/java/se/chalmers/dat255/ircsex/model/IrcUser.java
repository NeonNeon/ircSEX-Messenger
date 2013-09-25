package se.chalmers.dat255.ircsex.model;

/**
 * Created by Oskar on 2013-09-24.
 */
public class IrcUser {

    private String nick;
    private boolean op;
    private boolean voice;
    private boolean owner;

    public static final char OP = '@';
    public static final char VOICE = '+';
    public static final char OWNER = '~';
    public static final char NO_STATUS = (char) -1;

    public IrcUser(String user) {
    }


    /**
     * Extracts the status sign in a username.
     *
     * @param username Username to extract status sign from
     * @return Status sign
     */
    public static char extractUserStatus(String username) {
        char first = username.charAt(0);
        if (first == OP || first == VOICE || first == OWNER) {
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
