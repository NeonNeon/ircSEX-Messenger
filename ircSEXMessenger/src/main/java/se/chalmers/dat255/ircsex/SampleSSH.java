package se.chalmers.dat255.ircsex;

import net.schmizz.sshj.*;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

/**
 * Created by oed on 10/8/13.
 */
public class SampleSSH implements HostKeyVerifier {

    private static String addr = "levelinver.se";
    private static String user = "ircsex";
    private static String pass = "l";

    public static void main(String[] arrgs) throws IOException {
        new SampleSSH();
    }
    public SampleSSH() throws IOException {
        final SSHClient ssh = new SSHClient();

        ssh.addHostKeyVerifier(this);
        ssh.connect(addr);

        try {
            ssh.authPassword(user, pass);

            /*
            * _We_ listen on localhost:8080 and forward all connections on to server, which then forwards it to
            * google.com:80
            */
            final LocalPortForwarder.Parameters params
                    = new LocalPortForwarder.Parameters("0.0.0.0", 1337, "localhost", 4444);
            final ServerSocket ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(params.getLocalPort()));
            try {
                ssh.newLocalPortForwarder(params, ss).listen();
            } finally {
                ss.close();
            }

        } finally {
            ssh.disconnect();
        }

    }

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        return true;
    }
}
