package xyz.nulldev.phouse3.gson;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */

import xyz.nulldev.phouse3.SHARED.FastMousePacket;
import xyz.nulldev.phouse3.math.EulerAngles;

/**
 * Does all the math and stuff
 */

/**
 * TODO Learn quaternions since gimbal lock occurs with euler angles
 */
public class MouseManager {

    public static final float THRESHOLD = 0.5f;

    EulerAngles neutral;
    EulerAngles rightMost;
    EulerAngles leftMost;
    EulerAngles topMost;
    EulerAngles bottomMost;
    boolean calibrated = false;
    //Percentage from 0 - 1
    float x;
    float y;

    public MouseManager() {}

    public void calibrate(EulerAngles neutral, EulerAngles rightmost, EulerAngles leftMost, EulerAngles topMost, EulerAngles bottomMost) {
        this.neutral = neutral;
        this.rightMost = rightmost;
        this.leftMost = leftMost;
        this.topMost = topMost;
        this.bottomMost = bottomMost;
        this.calibrated = true;
    }

    public void update(EulerAngles update) {
        x = (update.getYaw() - leftMost.getYaw())/(rightMost.getYaw() - leftMost.getYaw());
        y = (update.getPitch() - topMost.getPitch())/(bottomMost.getPitch() - topMost.getPitch());
    }

    public FastMousePacket constructPacket() {
        return new FastMousePacket(x, y);
    }

    public EulerAngles getNeutral() {
        return neutral;
    }

    public void setNeutral(EulerAngles neutral) {
        this.neutral = neutral;
    }

    public EulerAngles getRightMost() {
        return rightMost;
    }

    public void setRightMost(EulerAngles rightMost) {
        this.rightMost = rightMost;
    }

    public EulerAngles getLeftMost() {
        return leftMost;
    }

    public void setLeftMost(EulerAngles leftMost) {
        this.leftMost = leftMost;
    }

    public EulerAngles getTopMost() {
        return topMost;
    }

    public void setTopMost(EulerAngles topMost) {
        this.topMost = topMost;
    }

    public EulerAngles getBottomMost() {
        return bottomMost;
    }

    public void setBottomMost(EulerAngles bottomMost) {
        this.bottomMost = bottomMost;
    }

    public boolean isCalibrated() {
        return calibrated;
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
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
