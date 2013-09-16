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
    public void connectScenarioTest() {
        ipa.connect("tord", "tord", "asdgrew haha");
        while (true) {

        }
    }
}
