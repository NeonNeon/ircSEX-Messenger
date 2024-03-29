package se.chalmers.dat255.ircsex.irc;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.Random;

/**
 * Created by oed on 10/10/13.
 */
public class SSHFlavor implements Flavor, HostKeyVerifier {

    private static final String LOCALHOST = "localhost";
    private static final int MIN_PORT = 49152;
    private static final int MAX_PORT = 65535;
    private final int LOCALPORT = getLocalPort();
    private String sshAddress;
    private String sshUser;
    private String sshPass;
    private String ircHost;
    private int ircPort;
    private Flavor socketFlavor;
    private boolean tunnelCreated;

    private SSHClient ssh;
    private ServerSocket serverSocket;

    /**
     * Create a taste that uses ssh.
     *
     * @param sshAddress - the address to the ssh server
     * @param sshUser - the ssh user
     * @param sshPass - the password for the ssh user
     * @param ircPort - the ircPort to use for the irc server
     */
    public SSHFlavor(String sshAddress, String sshUser, String sshPass,
                     String ircHost, int ircPort,
                     Class<? extends Flavor> socketFlavor) {
        this.sshAddress = sshAddress;
        this.sshUser = sshUser;
        this.sshPass = sshPass;
        this.ircHost = ircHost;
        this.ircPort = ircPort;
        try {
            this.socketFlavor = socketFlavor.getConstructor(String.class, int.class)
                    .newInstance(LOCALHOST, LOCALPORT);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        tunnelCreated = false;
    }

    @Override
    public BufferedReader getInput() throws IOException {
        checkTunnel();
        return socketFlavor.getInput();
    }

    @Override
    public BufferedWriter getOutput() throws IOException {
        checkTunnel();
        return socketFlavor.getOutput();
    }

    @Override
    public void close() throws IOException {
        socketFlavor.close();
        ssh.close();
    }

    private void checkTunnel() throws IOException {
        if (!tunnelCreated) {
            createTunnel();
            tunnelCreated = true;
        }
    }

    private void createTunnel() throws IOException {
        ssh = new SSHClient();
        ssh.addHostKeyVerifier(this);
        ssh.connect(sshAddress);
        ssh.authPassword(sshUser, sshPass);

        final LocalPortForwarder.Parameters params
                = new LocalPortForwarder.Parameters(LOCALHOST, LOCALPORT, ircHost, ircPort);

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(params.getLocalPort()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ssh.newLocalPortForwarder(params, serverSocket).listen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static int getLocalPort() {
        return new Random().nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;
    }

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        return true;
    }
}
