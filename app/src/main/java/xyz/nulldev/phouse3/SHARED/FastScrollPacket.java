package xyz.nulldev.phouse3.SHARED;

import java.nio.ByteBuffer;

/**
 * Project: Phouse3
 * Created: 31/01/16
 * Author: hc
 */
public class FastScrollPacket {
    float amount = 0;
    int direction = 0;

    public FastScrollPacket(float amount, int direction) {
        this.amount = amount;
        this.direction = direction;
    }

    public byte[] asPacket() {
        byte[] buffer = new byte[10];
        buffer[0] = 20;
        buffer[1] = (byte) direction;
        System.arraycopy(floatToBytes(amount), 0, buffer, 2, 4);
        return buffer;
    }

    public static FastScrollPacket fromPacket(byte[] buffer) {
        byte[] amountBytes = new byte[4];
        System.arraycopy(buffer, 2, amountBytes, 0, 4);
        return new FastScrollPacket(bytesToFloat(amountBytes),
                amountBytes[1]);
    }

    static byte[] floatToBytes(float f) {
        return ByteBuffer.allocate(4).putFloat(f).array();
    }

    static float bytesToFloat(byte[] b) {
        return ByteBuffer.wrap(b).getFloat();
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
