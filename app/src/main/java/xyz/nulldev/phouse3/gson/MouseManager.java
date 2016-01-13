package xyz.nulldev.phouse3.gson;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

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
    float origX;
    float origY;
    float x;
    float y;
    int upComp = 0;
    int leftComp = 0;
    int bottomComp = 0;
    int rightComp = 0;
    double[] xxPoints = null;
    double[] xyPoints = {0, 0.5, 1};
    double[] yxPoints = null;
    double[] yyPoints = {0, 0.5, 1};
    boolean disableInterpolation = false;

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
        origX = (update.getYaw() - leftMost.getYaw())/(rightMost.getYaw() - leftMost.getYaw());
        x = (float) PolynomialFunctionLagrangeForm.evaluate(xxPoints, xyPoints, update.getYaw());
        if(update.getYaw() > neutral.getYaw()) {
            //Right
            x += calcParabola(x, rightComp);
        } else if(update.getYaw() < neutral.getYaw()) {
            //Left
            x -= calcParabola(x, leftComp);
        }
        origY = (update.getPitch() - topMost.getPitch())/(bottomMost.getPitch() - topMost.getPitch());
        y = (float) PolynomialFunctionLagrangeForm.evaluate(yxPoints, yyPoints, update.getPitch());
        if(update.getPitch() > neutral.getPitch()) {
            //Down
            y += calcParabola(y, bottomComp);
        } else if(update.getPitch() < neutral.getPitch()) {
            //Up
            y -= calcParabola(y, upComp);
        }
    }

    public static float calcParabola(float original, float compensation) {
        //Log.i(Constants.TAG, "APPLYING: " + adjustCompensation(compensation) * (original*original));
        return adjustCompensation(compensation) * (original*original);
    }

    public static float adjustCompensation(float compensation) {
        return compensation/500;
    }

    public FastMousePacket constructPacket() {
        if (disableInterpolation)
            return new FastMousePacket(origX, origY);
        else
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
        //Setup arrays
        if(calibrated) {
            xxPoints = new double[]{getLeftMost().getYaw(), getNeutral().getYaw(), getRightMost().getYaw()};
            yxPoints = new double[]{getTopMost().getPitch(), getNeutral().getPitch(), getBottomMost().getPitch()};
        }
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

    public float getOrigX() {
        return origX;
    }

    public void setOrigX(float origX) {
        this.origX = origX;
    }

    public float getOrigY() {
        return origY;
    }

    public void setOrigY(float origY) {
        this.origY = origY;
    }

    public int getUpComp() {
        return upComp;
    }

    public void setUpComp(int upComp) {
        this.upComp = upComp;
    }

    public int getLeftComp() {
        return leftComp;
    }

    public void setLeftComp(int leftComp) {
        this.leftComp = leftComp;
    }

    public int getBottomComp() {
        return bottomComp;
    }

    public void setBottomComp(int bottomComp) {
        this.bottomComp = bottomComp;
    }

    public int getRightComp() {
        return rightComp;
    }

    public void setRightComp(int rightComp) {
        this.rightComp = rightComp;
    }

    public boolean isDisableInterpolation() {
        return disableInterpolation;
    }

    public void setDisableInterpolation(boolean disableInterpolation) {
        this.disableInterpolation = disableInterpolation;
    }
}
