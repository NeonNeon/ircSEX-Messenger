package se.chalmers.dat255.ircsex.irc;

import se.chalmers.dat255.ircsex.model.ServerConnectionData;

/**
 * Created by oed on 10/10/13.
 */
public class Brewery {

    public static IrcProtocolAdapter getNormalIPA(String host,
                                                  int port,
                                                  IrcProtocolListener listener) {
        return getIPAWithTaste(new NormalTaste(host, port), listener);
    }

    public static IrcProtocolAdapter getSSHIPA(String address, String user,
                                               String pass, String ircHost, int ircPort,
                                               IrcProtocolListener listener) {
        return getIPAWithTaste(new SSHTaste(address, user, pass, ircHost, ircPort, NormalTaste.class), listener);
    }

    private static IrcProtocolAdapter getIPAWithTaste(Taste taste,
                                                      IrcProtocolListener listener) {
        return new IrcProtocolAdapter(taste, listener);
    }

    public static IrcProtocolAdapter getIPA(ServerConnectionData data,
                                            IrcProtocolListener listener) {
        if (data.isUsingSsh()) {
            return getSSHIPA(data.getSshHostname(), data.getSshUsername(), data.getSshPassword(),
                    data.getServer(),data.getPort(), listener);
        }
        return getNormalIPA(data.getServer(), data.getPort(), listener);
    }
}
