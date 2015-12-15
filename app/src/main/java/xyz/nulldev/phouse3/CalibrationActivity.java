package xyz.nulldev.phouse3;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import xyz.nulldev.phouse3.gson.MouseManager;
import xyz.nulldev.phouse3.math.EulerAngles;
import xyz.nulldev.phouse3.sensor.CalibratedGyroscopeProvider;
import xyz.nulldev.phouse3.util.ContextUtils;
import xyz.nulldev.phouse3.util.Utils;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */
public class CalibrationActivity extends AppCompatActivity {

    public static final int CALIBRATION_REQUEST = 15;
    public static final int REQUEST_OK = 14;
    public static final int REQUEST_CANCELED = 13;

    MouseManager mouseManager;
    CoordinatorLayout coordinatorLayout;
    Button topButton;
    Button leftButton;
    Button neutralButton;
    Button rightButton;
    Button bottomButton;

    CalibratedGyroscopeProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callib);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mouseManager = new MouseManager();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
        topButton = (Button) findViewById(R.id.topBtn);
        topButton.setOnLongClickListener(v -> {
            onLongClickCalib(v);
            return false;
        });
        leftButton = (Button) findViewById(R.id.leftBtn);
        leftButton.setOnLongClickListener(v -> {
            onLongClickCalib(v);
            return false;
        });
        neutralButton = (Button) findViewById(R.id.neutralBtn);
        neutralButton.setOnLongClickListener(v -> {
            onLongClickCalib(v);
            return false;
        });
        rightButton = (Button) findViewById(R.id.rightBtn);
        rightButton.setOnLongClickListener(v -> {
            onLongClickCalib(v);
            return false;
        });
        bottomButton = (Button) findViewById(R.id.bottomBtn);
        bottomButton.setOnLongClickListener(v -> {
            onLongClickCalib(v);
            return false;
        });

        provider = new CalibratedGyroscopeProvider((SensorManager) getSystemService(SENSOR_SERVICE));
        provider.start();
    }

    public void onClickCalib(View view) {
        if(view == null) return;
        CalibrationType target;
        if(view.equals(topButton)) {
            target = CalibrationType.TOP;
        } else if(view.equals(leftButton)) {
            target = CalibrationType.LEFT;
        } else if(view.equals(neutralButton)) {
            target = CalibrationType.NEUTRAL;
        } else if(view.equals(rightButton)) {
            target = CalibrationType.RIGHT;
        } else if(view.equals(bottomButton)) {
            target = CalibrationType.BOTTOM;
        } else {
            ContextUtils.doSnackbar(coordinatorLayout, "Unknown target!", Snackbar.LENGTH_SHORT);
            return;
        }
        doCalib(target, provider.getEulerAngles());
        updateButtonStates();
        ContextUtils.doSnackbar(coordinatorLayout, "Calibrated target: " + target + "!", Snackbar.LENGTH_SHORT);
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("mm", Utils.getGson().toJson(mouseManager));
        if(mouseManager.isCalibrated()) {
            setResult(REQUEST_OK, result);
        } else {
            setResult(REQUEST_CANCELED, result);
        }
        finish();
    }

    public void onLongClickCalib(View view) {
        if(view == null) return;
        CalibrationType target;
        if(view.equals(topButton)) {
            target = CalibrationType.CANCEL_TOP;
        } else if(view.equals(leftButton)) {
            target = CalibrationType.CANCEL_LEFT;
        } else if(view.equals(neutralButton)) {
            target = CalibrationType.CANCEL_NEUTRAL;
        } else if(view.equals(rightButton)) {
            target = CalibrationType.CANCEL_RIGHT;
        } else if(view.equals(bottomButton)) {
            target = CalibrationType.CANCEL_BOTTOM;
        } else {
            ContextUtils.doSnackbar(coordinatorLayout, "Unknown target!", Snackbar.LENGTH_SHORT);
            return;
        }
        doCalib(target, null);
        updateButtonStates();
        ContextUtils.doSnackbar(coordinatorLayout, "Cleared target: " + target + "!", Snackbar.LENGTH_SHORT);
    }

    public void greenifyButton(View view, boolean doGreen) {
        if(doGreen) {
            view.getBackground().setColorFilter(Color.parseColor("#76ff03"), PorterDuff.Mode.MULTIPLY);
        } else {
            view.getBackground().clearColorFilter();
        }
    }

    public void updateButtonStates() {
        greenifyButton(topButton, isCalib(CalibrationType.TOP));
        greenifyButton(leftButton, isCalib(CalibrationType.LEFT));
        greenifyButton(neutralButton, isCalib(CalibrationType.NEUTRAL));
        greenifyButton(rightButton, isCalib(CalibrationType.RIGHT));
        greenifyButton(bottomButton, isCalib(CalibrationType.BOTTOM));
    }

    public void updateCalibState() {
        mouseManager.setCalibrated(mouseManager.getTopMost() != null
                && mouseManager.getLeftMost() != null
                && mouseManager.getNeutral() != null
                && mouseManager.getRightMost() != null
                && mouseManager.getBottomMost() != null);
    }

    public boolean isCalib(CalibrationType type) {
        switch (type) {
            case TOP:
                return mouseManager.getTopMost() != null;
            case LEFT:
                return mouseManager.getLeftMost() != null;
            case NEUTRAL:
                return mouseManager.getNeutral() != null;
            case RIGHT:
                return mouseManager.getRightMost() != null;
            case BOTTOM:
                return mouseManager.getBottomMost() != null;
        }
        throw new IllegalArgumentException("Invalid calibration type! (" + type + ")");
    }

    public void doCalib(CalibrationType type, EulerAngles angles) {
        switch(type) {
            case TOP:
                mouseManager.setTopMost(angles);
                break;
            case LEFT:
                mouseManager.setLeftMost(angles);
                break;
            case NEUTRAL:
                mouseManager.setNeutral(angles);
                break;
            case RIGHT:
                mouseManager.setRightMost(angles);
                break;
            case BOTTOM:
                mouseManager.setBottomMost(angles);
                break;
            case CANCEL_TOP:
                mouseManager.setTopMost(null);
                break;
            case CANCEL_LEFT:
                mouseManager.setLeftMost(null);
                break;
            case CANCEL_NEUTRAL:
                mouseManager.setNeutral(null);
                break;
            case CANCEL_RIGHT:
                mouseManager.setRightMost(null);
                break;
            case CANCEL_BOTTOM:
                mouseManager.setBottomMost(null);
                break;
            default:
                return;
        }
        updateCalibState();
    }

    enum CalibrationType {
        TOP,
        LEFT,
        NEUTRAL,
        RIGHT,
        BOTTOM,
        CANCEL_TOP,
        CANCEL_LEFT,
        CANCEL_NEUTRAL,
        CANCEL_RIGHT,
        CANCEL_BOTTOM
    }
}
