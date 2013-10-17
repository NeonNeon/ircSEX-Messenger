package se.chalmers.dat255.ircsex.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by Alexander Hultnér on 2013-10-17.
 *
 * Reference: http://stackoverflow.com/questions/887148/how-to-determine-if-a-string-contains-invalid-encoded-characters
 * Open issue here: http://stackoverflow.com/questions/19427822/android-encoding-incoming-irc-socket
 *
 */
public class Encoding {

    /**
     * Checks if encoding is correct, throws exception if not.
     *
     * @param msg
     * @throws UnsupportedEncodingException
     */
    public static void checkEncoding(String msg) throws UnsupportedEncodingException {

        byte[] bytes = msg.getBytes("ISO-8859-1");
        if (validateUTF8(bytes)){
            throw new UnsupportedEncodingException("MSG not UTF8");
        }

    }

    /**
     * Checks for encoding and converts the string to appropriate encoding.
     *
     * @param msg
     * @return msg in correct encoding
     */
    public static String fixEncoding(String msg) {
        try {
            byte[] bytes = msg.getBytes("ISO-8859-1");
            if (!validateUTF8(bytes))
                return msg;
            Log.d("IRCDEBUG", "validUTF8: "+new String(bytes, "UTF-8"));
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Impossible, throw unchecked
            throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
        }

    }

    /**
     * Valdiates if byte[] input is a UTF8-string
     *
     * @param input
     * @return
     */
    public static boolean validateUTF8(byte[] input) {
        int i = 0;
        // Check for BOM
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
                && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int octet = input[i];
            if ((octet & 0x80) == 0) {
                continue; // ASCII
            }

            // Check for UTF-8 leading byte
            if ((octet & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((octet & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((octet & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                // Java only supports BMP so 3 is max
                return false;
            }

            while (i < end) {
                i++;
                octet = input[i];
                if ((octet & 0xC0) != 0x80) {
                    // Not a valid trailing byte
                    return false;
                }
            }
        }
        return true;
    }

}
