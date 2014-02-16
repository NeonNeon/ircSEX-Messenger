package se.chalmers.dat255.ircsex.model;

import se.chalmers.dat255.ircsex.irc.Flavor;
import se.chalmers.dat255.ircsex.irc.IrcProtocolAdapter;
import se.chalmers.dat255.ircsex.irc.IrcProtocolListener;
import se.chalmers.dat255.ircsex.irc.NormalFlavor;
import se.chalmers.dat255.ircsex.irc.SSHFlavor;
import se.chalmers.dat255.ircsex.irc.ServerConnectionData;

/**
 * Created by oed on 10/10/13.
 */
public class Brewery {

    public static IrcProtocolAdapter getNormalIPA(String host,
                                                  int port,
                                                  IrcProtocolListener listener) {
        return getIPAWithTaste(new NormalFlavor(host, port), listener);
    }

    public static IrcProtocolAdapter getSSHIPA(String address, String user,
                                               String pass, String ircHost, int ircPort,
                                               IrcProtocolListener listener) {
        return getIPAWithTaste(new SSHFlavor(address, user, pass, ircHost, ircPort, NormalFlavor.class), listener);
    }

    private static IrcProtocolAdapter getIPAWithTaste(Flavor flavor,
                                                      IrcProtocolListener listener) {
        return new IrcProtocolAdapter(flavor, listener);
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
