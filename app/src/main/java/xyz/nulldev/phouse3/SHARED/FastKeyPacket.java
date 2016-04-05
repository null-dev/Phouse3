package xyz.nulldev.phouse3.SHARED;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import xyz.nulldev.phouse3.Constants;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */
public class FastKeyPacket {
    char key;

    public FastKeyPacket(char key) {
        this.key = key;
    }

    public byte[] asPacket() {
        byte[] buffer = new byte[10];
        buffer[0] = 10;
        byte[] asBytes;
        Log.i(Constants.TAG, "KEY: " + key);
        try {
            asBytes = String.valueOf(key).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("Critical charset missing!");
        }
        byte length = 0;
        for(byte b : asBytes) {
            length++;
        }
        buffer[1] = length;
        if(length > 8) {
            throw new IllegalArgumentException("Character too big!");
        }
        System.arraycopy(asBytes, 0, buffer, 2, length);
        Log.i(Constants.TAG, "BYTES: " + Arrays.toString(buffer));
        return buffer;
    }

    public static FastKeyPacket fromPacket(byte[] buffer) {
        byte length = buffer[1];
        byte[] charBytes = new byte[length];
        System.arraycopy(buffer, 2, charBytes, 0, length);
        return new FastKeyPacket(new String(charBytes, Charset.forName("UTF-8")).toCharArray()[0]);
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }
}
