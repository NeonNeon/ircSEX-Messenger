package se.chalmers.dat255.ircsex.irc;

/**
 * Created by oed on 10/11/13.
 */
public class ServerConnectionData {

    private String server;
    private int port;
    private String nickname;
    private String login;
    private String realname;
    private String password;
    private boolean usingSsl;
    private boolean usingSsh;
    private String sshHostname;
    private String sshUsername;
    private String sshPassword;

    public ServerConnectionData(String server, int port, String nickname, String login, String realname,
                                String password, boolean usingSsl, boolean usingSsh,
                                String sshHostname, String sshUsername, String sshPassword) {
        this.server = server;
        this.port = port;
        this.nickname = nickname;
        this.login = login;
        this.realname = realname;
        this.password = password;
        this.usingSsl = usingSsl;
        this.usingSsh = usingSsh;
        this.sshHostname = sshHostname;
        this.sshUsername = sshUsername;
        this.sshPassword = sshPassword;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public String getNickname() {
        return nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getRealname() {
        return realname;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUsingSsl() {
        return usingSsl;
    }

    public boolean isUsingSsh() {
        return usingSsh;
    }

    public String getSshHostname() {
        return sshHostname;
    }

    public String getSshUsername() {
        return sshUsername;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
