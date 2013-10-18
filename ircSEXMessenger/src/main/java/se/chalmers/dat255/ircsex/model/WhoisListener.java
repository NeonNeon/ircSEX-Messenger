package se.chalmers.dat255.ircsex.model;

import java.util.List;

/**
 * Created by Oskar on 2013-10-12.
 */
public interface WhoisListener {

    /**
     * This method sends a list of connected channels as a whois request resopnse.
     * @param nick - the nick of the requested whois
     * @param channels - the channels the nick is connected to
     */
    public void whoisChannels(String nick, List<String> channels);

    /**
     * This method sends the nicks realname as a whois request resopnse.
     * @param nick
     * @param realname
     */
    public void whoisRealname(String nick, String realname);

    /**
     * This method sends the nicks idletime as a whois request resopnse.
     * @param nick
     * @param formattedIdleTime
     */
    public void whoisIdleTime(String nick, String formattedIdleTime);
}
