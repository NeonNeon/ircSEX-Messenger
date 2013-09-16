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
        try {
            ipa = new IrcProtocolAdapter(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(ipa).start();
    }

    @Test
    public void connectDisconnectScenarioTest() throws InterruptedException {
        ipa.connect("tord", "tord", "asdgrew haha");

        // This test dosen't really do anything.
        Thread.sleep(9999);

        ipa.disconnect("ircSEX FTW");
        while (true) {

        }
    }

}
