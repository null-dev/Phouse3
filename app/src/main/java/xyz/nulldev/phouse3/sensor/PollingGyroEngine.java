package xyz.nulldev.phouse3.sensor;

import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nulldev.phouse3.Constants;
import xyz.nulldev.phouse3.concurrent.ObservableObject;
import xyz.nulldev.phouse3.concurrent.OnChangeListener;

/**
 * Project: Phouse3
 * Created: 14/12/15
 * Author: hc
 */
public class PollingGyroEngine {

    public static final int POLLING_INTERVAL = 50;

    OrientationProvider gyroProvider;
    SensorManager sensorManager;
    ObservableObject<GyroscopeResult> result = new ObservableObject<>();
    ObservableObject<GyroscopeResult> tmpResult = null;
    InternalPoller internalPoller;
    Thread internalPollerThread;

    public PollingGyroEngine(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        gyroProvider = new CalibratedGyroscopeProvider(sensorManager);
//        gyroProvider = new ImprovedOrientationSensor1Provider(sensorManager);
//        gyroProvider = new AccelerometerCompassProvider(sensorManager);
        gyroProvider.start();
        internalPoller = new InternalPoller();
        internalPollerThread = new Thread(internalPoller,
                "Phouse3 > Sensor Poll Thread");
        internalPollerThread.start();
        Log.i(Constants.TAG, "Polling engine started!");
    }

    public void pause() {
        if(!isPaused()) {
            Log.i(Constants.TAG, "Polling engine paused!");
            tmpResult = result;
            result = new ObservableObject<>();
        }
    }

    public boolean isPaused() {
        return tmpResult != null;
    }

    public void unpause() {
        if(isPaused()) {
            Log.i(Constants.TAG, "Polling engine unpaused!");
            result = tmpResult;
            tmpResult = null;
        }
    }

    public void destroy() {
        if(internalPoller != null) {
            internalPoller.die();
            internalPoller = null;
        }
        if(internalPollerThread != null) {
            internalPollerThread.interrupt();
            internalPollerThread = null;
        }
        if (gyroProvider != null) {
            gyroProvider.stop();
            gyroProvider = null;
        }
        sensorManager = null;
        if(result != null) {
            result.set(null);
            result = null;
        }
        Log.i(Constants.TAG, "Polling engine destroyed!");
    }

    public GyroscopeResult getResult() {
        if(result == null) return null;
        return result.get();
    }

    public void addListener(OnChangeListener<GyroscopeResult> listener) {
        result.addChangeListener(listener);
    }

    public void removeListener(OnChangeListener<GyroscopeResult> listener) {
        result.getChangeListeners().remove(listener);
    }

    public ArrayList<OnChangeListener<GyroscopeResult>> getListeners() {
        return result.getChangeListeners();
    }

    private class InternalPoller implements Runnable {

        private final AtomicBoolean die = new AtomicBoolean(false);

        public void die() {
            this.die.set(true);
        }

        @Override
        public void run() {
            while(!die.get()) {
                if(gyroProvider != null)
                    result.set(new GyroscopeResult(gyroProvider.getQuaternion(),
                            gyroProvider.getRotationMatrix(),
                            gyroProvider.getEulerAngles()));
                try {
                    Thread.sleep(POLLING_INTERVAL);
                } catch (InterruptedException e) {return;}
            }
        }
    }
}
