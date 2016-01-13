package xyz.nulldev.phouse3.SHARED;

import java.nio.ByteBuffer;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */
public class FastTargetPacket {
    float x;
    float y;

    public FastTargetPacket(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public byte[] asPacket() {
        byte[] buffer = new byte[10];
        buffer[0] = 8;
        System.arraycopy(floatToBytes(x), 0, buffer, 1, 4);
        System.arraycopy(floatToBytes(y), 0, buffer, 5, 4);
        return buffer;
    }

    public static FastTargetPacket fromPacket(byte[] buffer) {
        byte[] xBytes = new byte[4];
        byte[] yBytes = new byte[4];
        System.arraycopy(buffer, 1, xBytes, 0, 4);
        System.arraycopy(buffer, 5, yBytes, 0, 4);
        return new FastTargetPacket(bytesToFloat(xBytes),
                bytesToFloat(yBytes));
    }

    static byte[] floatToBytes(float f) {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    static float bytesToFloat(byte[] b) {
        return ByteBuffer.wrap(b).getFloat();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
