package se.chalmers.dat255.ircsex.irc;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

/**
 * Created by oed on 10/10/13.
 */
public class SSHTaste implements Taste, HostKeyVerifier {

    private static final String LOCALHOST = "localhost";
    private String address;
    private String user;
    private String pass;
    private int ircPort;
    private boolean socketCreated;

    private SSHClient ssh;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    /**
     * Create a taste that uses ssh.
     *
     * @param address - the address to the ssh server
     * @param user - the ssh user
     * @param pass - the password for the ssh user
     * @param ircPort - the ircPort to use for the irc server
     */
    public SSHTaste(String address, String user, String pass, int ircPort) {
        this.address = address;
        this.user = user;
        this.pass = pass;
        this.ircPort = ircPort;
        socketCreated = false;
    }

    @Override
    public BufferedReader getInput() throws IOException {
        checkSocket();
        return input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public BufferedWriter getOutput() throws IOException {
        checkSocket();
        return output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    private void checkSocket() throws IOException {
        if (!socketCreated) {
            createSocket();
            socketCreated = true;
        }
    }

    private void createSocket() throws IOException {
        createSSHTunnel();
        socket = new Socket(LOCALHOST, 1337);
    }

    private void createSSHTunnel() throws IOException {
        ssh = new SSHClient();
        ssh.addHostKeyVerifier(this);
        ssh.connect(address);
        ssh.authPassword(user, pass);

        final LocalPortForwarder.Parameters params
                = new LocalPortForwarder.Parameters("0.0.0.0", 1337, "localhost", ircPort);

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

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        // TODO - verify for real
        return true;
    }
}
