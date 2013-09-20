package se.chalmers.dat255.ircsex.irc;

import org.junit.*;

import java.io.IOException;

/**
 * Created by oed on 9/16/13.
 */
public class IrcProtocolAdapterTest {
    private static IrcProtocolAdapter ipa;
    @BeforeClass
    public static void setup() {
        String server = "irc.chalmers.it";
        int port = 6667;
        ipa = new IrcProtocolAdapter(server, port);
        new Thread(ipa).start();
    }

    @Test
    public void connectDisconnectScenarioTest() throws InterruptedException {
        ipa.addIrcProtocolServerListener(new IrcProtocolAdapter.IrcProtocolServerListener() {
            @Override
            public void fireEvent(IrcProtocolAdapter.MessageType type, String message) {
                if (message == IrcProtocolAdapter.Messages.IOConnected) {
                    ipa.connect("tord", "tord", "asdgrew haha");
                    // This test dosen't really do anything.

                    //ipa.disconnect("ircSEX FTW");
                }

            }

            @Override
            public void fireChannelEvent(IrcProtocolAdapter.MessageType type, String channel, String message) {

            }
        });
        try {
            Thread.sleep(9999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ipa.partChannel("#itstud");
        while (true) {
        }

    }

}
