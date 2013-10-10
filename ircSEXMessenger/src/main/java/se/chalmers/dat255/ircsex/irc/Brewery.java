package se.chalmers.dat255.ircsex.irc;

/**
 * Created by oed on 10/10/13.
 */
public class Brewery {

    public static IrcProtocolAdapter getNormalIPA(String host,
                                                  int port,
                                                  IrcProtocolListener listener) {
        return getIPAWithTaste(new NormalTaste(host, port), listener);
    }

    private static IrcProtocolAdapter getIPAWithTaste(Taste taste,
                                                      IrcProtocolListener listener) {
        return new IrcProtocolAdapter(taste, listener);
    }
}
