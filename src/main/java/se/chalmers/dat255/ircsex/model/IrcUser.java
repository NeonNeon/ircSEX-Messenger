package se.chalmers.dat255.ircsex.model;

import android.graphics.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Oskar on 2013-09-24.
 */
public class IrcUser implements Comparable<IrcUser> {
    private String nick;
    private char status;
    private boolean owner;
    private boolean op;
    private boolean halfOp;
    private boolean voice;
    private boolean self;

    private int color;

    public static final char OWNER = '~';
    public static final char OP = '@';
    public static final char HALF_OP = '%';
    public static final char VOICE = '+';
    public static final char NO_STATUS = (char) -1;

    public IrcUser(String user, char status) {
        this.nick = user;
        this.status = status;

        owner = status == OWNER;
        op = status == OP;
        voice = status == VOICE;
        halfOp = status == HALF_OP;

        generateColor();
    }

    private void generateColor() {
        float hue;
        try {
            hue = generateHue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hue = 0;
        }
        float[] hsv = {hue, 0.05f, 0.99f};
        color = Color.HSVToColor(hsv);
    }

    private float generateHue() throws NoSuchAlgorithmException {
        byte[] bytes;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        bytes = md5.digest(nick.getBytes());
        float sum = 0;
        for (byte value : bytes) {
            sum += Math.abs(value);
        }
        return sum % 360;
    }

    /**
     * Returns the nickname.
     *
     * @return The nickname
     */
    public String getNick() {
        return nick;
    }

    public boolean isOwner() {
        return owner;
    }

    public boolean isOp() {
        return op;
    }

    public boolean isHalfOp() {
        return halfOp;
    }

    public boolean isVoice() {
        return voice;
    }

    public void changeNick(String nick) {
        this.nick = nick;
        generateColor();
    }

    @Override
    public String toString() {
        return (status == NO_STATUS ? "" : status) + nick;
    }

    @Override
    public int compareTo(IrcUser ircUser) {
        if (owner != ircUser.isOwner()) {
            return Boolean.valueOf(ircUser.isOwner()).compareTo(owner);
        } else if (op != ircUser.isOp()) {
            return Boolean.valueOf(ircUser.isOp()).compareTo(op);
        } else if (halfOp != ircUser.isHalfOp()) {
            return Boolean.valueOf(ircUser.isHalfOp()).compareTo(halfOp);
        } else if (voice != ircUser.isVoice()) {
            return Boolean.valueOf(ircUser.isVoice()).compareTo(voice);
        } else {
            return nick.toLowerCase().compareTo(ircUser.getNick().toLowerCase());
        }
    }

    public int getColor() {
        return color;
    }

    public boolean isNamed(String name) {
        return nick.equals(name);
    }

    /**
     * Extracts the status sign in a username.
     *
     * @param username Username to extract status sign from
     * @return Status sign
     */
    public static char extractUserStatus(String username) {
        char first = username.charAt(0);
        if (first == OP || first == VOICE || first == OWNER || first == HALF_OP) {
            return first;
        } else {
            return NO_STATUS;
        }
    }

    /**
     * Extracts the username from the string status+username.
     *
     * @param username Username to extract from
     * @return Extracted username
     */
    public static String extractUserName(String username) {
        char status = extractUserStatus(username);
        return username.replace(Character.toString(status), "");
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf() {
        this.self = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IrcUser ircUser = (IrcUser) o;
        return nick.equals(ircUser.nick);
    }

    @Override
    public int hashCode() {
        return nick.hashCode();
    }

    public static String formatIdleTime(int time) {
        int seconds = time % 60;
        time /= 60;
        int minutes = time % 60;
        time /= 60;
        int hours = time % 24;
        int days = time / 24;

        String idle = "";
        if (days > 0) {
            idle += " " + days + "d";
        } if (hours > 0) {
            idle += " " + hours + "h";
        } if (minutes > 0) {
            idle += " " + minutes + "m";
        } if (seconds > 0) {
            idle += " " + seconds + "s";
        }

        return idle.trim();
    }
}
