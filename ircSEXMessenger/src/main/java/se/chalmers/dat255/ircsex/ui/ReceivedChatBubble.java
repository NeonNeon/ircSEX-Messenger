package se.chalmers.dat255.ircsex.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import se.chalmers.dat255.ircsex.R;

/**
 * Created by Johan on 2013-09-24.
 */
public class ReceivedChatBubble extends ChatBubble {
    private final String nick;

    protected ReceivedChatBubble(String nick, String message) {
        super(message);
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public int getGravity() {
        return Gravity.LEFT;
    }

    @Override
    public Rect getPadding() {
        return new Rect(40, 20, 30, 30);
    }

    @Override
    public int getColor() {
        float hue;
        try {
            hue = generateHue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hue = 0;
        }
        float[] hsv = {hue, 0.15f, 0.8f};
        return Color.HSVToColor(hsv);
    }

    private float generateHue() throws NoSuchAlgorithmException {
        byte[] bytes;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        bytes = md5.digest(nick.getBytes());
        float steklek = 0;
        for (byte value : bytes) {
            steklek += Math.abs(value);
        }
        return steklek % 360;
    }

    @Override
    public int getNinePatchID() {
        return R.drawable.left_chat_bubble;
    }

    @Override
    public int getLayoutID() {
        return R.layout.received_chat_bubble;
    }
}
