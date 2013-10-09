package se.chalmers.dat255.ircsex;

import net.schmizz.sshj.*;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

/**
 * Created by oed on 10/8/13.
 */
public class SampleSSH implements HostKeyVerifier {

    private static String addr = "hubben.chalmers.it";
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
            final Session session = ssh.startSession();

            try {
                final Session.Command cmd = session.exec("ping -c 1 google.com");
                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                System.out.println("\n** exit status: " + cmd.getExitStatus());
            } finally {
                session.close();
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
