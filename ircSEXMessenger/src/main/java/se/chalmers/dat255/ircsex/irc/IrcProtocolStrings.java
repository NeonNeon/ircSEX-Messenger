package se.chalmers.dat255.ircsex.irc;

/**
 * Created by oed on 10/5/13.
 */
public class IrcProtocolStrings {

    public static final String PRIVMSG = "PRIVMSG";
    public static final String PING = "PING";
    public static final String PONG = "PONG";
    public static final String JOIN = "JOIN";
    public static final String PART = "PART";
    public static final String NICK = "NICK";
    public static final String MODE = "MODE";
    public static final String USER = "USER";
    public static final String QUIT = "QUIT";
    public static final String NAMES = "NAMES";
    public static final String LIST = "LIST";
    public static final String WHOIS = "WHOIS";
    public static final String INVITE = "INVITE";

    // Numeric replies
    public static final String RPL_WHOISUSER = "311";
    public static final String RPL_WHOISIDLE = "317";
    public static final String RPL_WHOISCHANNELS = "319";
    public static final String RPL_LIST = "322";
    public static final String RPL_NAMREPLY = "353";

    // Numeric errors
    public static final String ERR_NICKNAMEINUSE = "433";

}
