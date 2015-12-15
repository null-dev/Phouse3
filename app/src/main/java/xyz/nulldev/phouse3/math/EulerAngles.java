package xyz.nulldev.phouse3.math;

import xyz.nulldev.phouse3.util.Utils;

/**
 * SOURCE: https://bitbucket.org/apacha/sensor-fusion-demo
 */
public class EulerAngles {

    //Limits and stuff
    public static final double YAW_MAX = Math.PI;
    public static final double PITCH_MAX = Math.PI/2;
    public static final double ROLL_MAX = Math.PI;
    public static final double YAW_MIN = -Math.PI;
    public static final double PITCH_MIN = -Math.PI/2;
    public static final double ROLL_MIN = -Math.PI;
//    public static final double YAW_MAX = 180;
//    public static final double PITCH_MAX = 180;
//    public static final double ROLL_MAX = 180;
//    public static final double YAW_MIN = -180;
//    public static final double PITCH_MIN = -180;
//    public static final double ROLL_MIN = -180;

    private final float yaw;
    private final float pitch;
    private final float roll;

    public EulerAngles(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public EulerAngles subtract(EulerAngles other) {
        EulerAngles newOther = new EulerAngles(other.getYaw()*-1, other.getPitch()*-1, other.getYaw()*-1);
        return add(newOther);
    }

    public EulerAngles add(EulerAngles other) {
        return new EulerAngles ((float) Utils.addWithWrapping(yaw, other.getYaw(), YAW_MIN, YAW_MAX),
                (float) Utils.addWithWrapping(pitch, other.getPitch(), PITCH_MIN, PITCH_MAX),
                (float) Utils.addWithWrapping(roll, other.getRoll(), ROLL_MIN, ROLL_MAX));
    }

    public EulerAngles diff(EulerAngles other) {
        float newYaw = Utils.getLargest(yaw, other.getYaw()) - Utils.getSmallest(yaw, other.getYaw());
        float newPitch = Utils.getLargest(pitch, other.getPitch()) - Utils.getSmallest(pitch, other.getPitch());
        float newRoll = Utils.getLargest(roll, other.getRoll()) - Utils.getSmallest(roll, other.getRoll());
        return new EulerAngles(newYaw, newPitch, newRoll);
    }

    public EulerAngles addWithThreshold(EulerAngles other, float thres) {
        return new EulerAngles (overThreshold(yaw, other.getYaw(), thres) ? (float) Utils.addWithWrapping(yaw, other.getYaw(), YAW_MIN, YAW_MAX) : yaw,
                overThreshold(pitch, other.getPitch(), thres) ? (float) Utils.addWithWrapping(pitch, other.getPitch(), PITCH_MIN, PITCH_MAX) : pitch,
                overThreshold(roll, other.getRoll(), thres) ? (float) Utils.addWithWrapping(roll, other.getRoll(), ROLL_MIN, ROLL_MAX) : roll);
    }

    boolean overThreshold(double d1, double d2, float thres) {
        return Math.abs(d1 - d2) > thres;
    }

    @Override
    public String toString() {
        return "EulerAngles{" +
                "roll=" + roll +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EulerAngles angles = (EulerAngles) o;

        return Float.compare(angles.yaw, yaw) == 0
                && Float.compare(angles.pitch, pitch) == 0
                && Float.compare(angles.roll, roll) == 0;
    }

    @Override
    public int hashCode() {
        int result = (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        result = 31 * result + (roll != +0.0f ? Float.floatToIntBits(roll) : 0);
        return result;
    }
}
