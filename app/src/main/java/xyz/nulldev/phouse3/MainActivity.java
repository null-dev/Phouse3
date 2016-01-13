package xyz.nulldev.phouse3;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import xyz.nulldev.phouse3.SHARED.FastKeyPacket;
import xyz.nulldev.phouse3.concurrent.DialogObservable;
import xyz.nulldev.phouse3.concurrent.QueuedBlockingObservableObject;
import xyz.nulldev.phouse3.gson.MouseManager;
import xyz.nulldev.phouse3.io.WIFIClient;
import xyz.nulldev.phouse3.math.EulerAngles;
import xyz.nulldev.phouse3.sensor.PollingGyroEngine;
import xyz.nulldev.phouse3.ui.PlayPauseFAB;
import xyz.nulldev.phouse3.ui.VirtualKeyboardButton;
import xyz.nulldev.phouse3.util.ConcurrencyUtils;
import xyz.nulldev.phouse3.util.ContextUtils;
import xyz.nulldev.phouse3.util.Utils;

public class MainActivity extends AppCompatActivity {

    LinearLayout errorLayout;
    RelativeLayout controlsLayout;
    PlayPauseFAB fab;
    TextView debugTextView;
    QueuedBlockingObservableObject<WIFIClient> wifiClient = new QueuedBlockingObservableObject<>();
    PollingGyroEngine gyroEngine;
    MouseManager mouseManager = null;
    VirtualKeyboardButton keyboardButton;
    SeekBar upComp;
    SeekBar leftComp;
    SeekBar bottomComp;
    SeekBar rightComp;
    Switch pauseDebugSwitch;
    Switch disableInterpolationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (PlayPauseFAB) findViewById(R.id.fab);
        fab.setPlaying(false);
        //All of this shit below is the connection and ui update code.
        //Note that although messy, this code is also very flexible. Multiple connection attempts can be started while one is still active and the code
        //will handle the connections properly and cancel the correct amount of connections.
        //We can further extend this stacking connection handling by using IP checks to cancel the correct connections (maybe later)
        fab.setOnClickListener(view -> {
            if(wifiClient.get() != null) {
                ConcurrencyUtils.runAsync(this::stopAll);
                updateUIOnDisconnect();
                fab.setPlaying(false);
                ContextUtils.doSnackbar(view, "Connection closed!", Snackbar.LENGTH_SHORT);
            } else {
                fab.setPlaying(true);
                Snackbar.make(view, "Connecting to server...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cancel", v -> {
                            Snackbar.make(view, "Connection aborted!", Snackbar.LENGTH_SHORT).show();
                            wifiClient.addSetListenerIfNotSet((n) -> {
                                try {
                                    n.disconnect();
                                } catch (IOException e1) {
                                    Log.w(Constants.TAG, "Exception while disconnecting on abort, ignoring...", e1);
                                }
                                fab.setPlaying(false);
                                return false;
                            });
                        }).show();
                new DialogObservable(MainActivity.this, "IP", "Please enter the ip to connect to!", "IP", "Ok", true)
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {}

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof DialogObservable.DialogCancelledException) {
                                    fab.setPlaying(false);
                                    ContextUtils.doSnackbar(view, "Connection aborted!", Snackbar.LENGTH_SHORT);
                                } else {
                                    new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onNext(String s) {
                                if (TextUtils.isEmpty(s)) {
                                    if (!wifiClient.getSetListeners().isEmpty())
                                        wifiClient.getSetListeners().poll();
                                    fab.setPlaying(false);
                                    ContextUtils.doSnackbar(view, "Connection aborted! (Invalid IP)", Snackbar.LENGTH_SHORT);
                                    return;
                                }
                                new Thread(() -> {
                                    new ConnectObservable(s)
                                            .subscribe(new Subscriber<WIFIClient>() {
                                                @Override
                                                public void onCompleted() {
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.e(Constants.TAG, "Connection failed!", e);
                                                    if (!wifiClient.getSetListeners().isEmpty())
                                                        wifiClient.getSetListeners().poll();
                                                    ConcurrencyUtils.runOnUiThread(() -> fab.setPlaying(false));
                                                    ContextUtils.doSnackbar(view, "Connection failed!", Snackbar.LENGTH_SHORT);
                                                }

                                                @Override
                                                public void onNext(WIFIClient wifiClient) {
                                                    MainActivity.this.wifiClient.set(wifiClient);
                                                    if (!MainActivity.this.wifiClient.isEmpty()) {
                                                        ContextUtils.doSnackbar(view, "Connection established!", Snackbar.LENGTH_SHORT);
                                                        ConcurrencyUtils.runOnUiThread(MainActivity.this::updateUIOnConnection);
                                                        Intent calib = new Intent(MainActivity.this, CalibrationActivity.class);
                                                        startActivityForResult(calib, CalibrationActivity.CALIBRATION_REQUEST);
                                                    }
                                                }
                                            });
                                }).start();
                            }
                        });
            }
        });

        errorLayout = (LinearLayout) findViewById(R.id.errorLayout);
        controlsLayout = (RelativeLayout) findViewById(R.id.controlsLayout);
        keyboardButton = (VirtualKeyboardButton) findViewById(R.id.keyboardView);

        debugTextView = (TextView) findViewById(R.id.debugTextView);
        pauseDebugSwitch = (Switch) findViewById(R.id.pauseDebugSwitch);
        disableInterpolationSwitch = (Switch) findViewById(R.id.disableInterpolationSwitch);
        disableInterpolationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mouseManager.setDisableInterpolation(isChecked));

        SeekBar.OnSeekBarChangeListener defaultListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateComp(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateComp(seekBar, seekBar.getProgress());
            }
        };

        (upComp = (SeekBar) findViewById(R.id.upComp)).setOnSeekBarChangeListener(defaultListener);
        (leftComp = (SeekBar) findViewById(R.id.leftComp)).setOnSeekBarChangeListener(defaultListener);
        (bottomComp = (SeekBar) findViewById(R.id.btmComp)).setOnSeekBarChangeListener(defaultListener);
        (rightComp = (SeekBar) findViewById(R.id.rightComp)).setOnSeekBarChangeListener(defaultListener);

        findViewById(R.id.leftClickButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(wifiClient != null && wifiClient.get() != null) {
                    wifiClient.get().write(new byte[]{6,0,0,0,0,0,0,0,0,0});
                }
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                if(wifiClient != null && wifiClient.get() != null) {
                    wifiClient.get().write(new byte[]{7,0,0,0,0,0,0,0,0,0});
                }
            }
            return false;
        });

        findViewById(R.id.rightClickButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if(wifiClient != null && wifiClient.get() != null) {
                    wifiClient.get().write(new byte[]{8,0,0,0,0,0,0,0,0,0});
                }
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                if(wifiClient != null && wifiClient.get() != null) {
                    wifiClient.get().write(new byte[]{9,0,0,0,0,0,0,0,0,0});
                }
            }
            return false;
        });

        updateUIOnDisconnect();
    }

    void updateComp(SeekBar view, int progress) {
        Log.i(Constants.TAG, "COMPENSATION: " + progress);
        if(mouseManager != null) {
            if(view.equals(upComp)) {
                mouseManager.setUpComp(progress);
            } else if(view.equals(leftComp)) {
                mouseManager.setLeftComp(progress);
            } else if(view.equals(bottomComp)) {
                mouseManager.setBottomComp(progress);
            } else if(view.equals(rightComp)) {
                mouseManager.setRightComp(progress);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CalibrationActivity.CALIBRATION_REQUEST) {
            if(resultCode == CalibrationActivity.REQUEST_CANCELED) {
                ConcurrencyUtils.runAsync(this::stopAll);
                updateUIOnDisconnect();
                fab.setPlaying(false);
                ContextUtils.doSnackbar(fab, "Calibration was cancelled!", Snackbar.LENGTH_SHORT);
                return;
            }
            Log.i(Constants.TAG, "Calibration result code: " + resultCode);
            mouseManager = Utils.getGson().fromJson(data.getStringExtra("mm"), MouseManager.class);
            ContextUtils.doSnackbar(fab, "Calibration successful!", Snackbar.LENGTH_SHORT);
            startSensorPolling();
        }
    }

    void updateDebugInfo(String message) {
        if (!pauseDebugSwitch.isChecked())
            debugTextView.setText(message);
    }

    void updateUIOnDisconnect() {
        ContextUtils.fadeView(errorLayout, true);
        ContextUtils.fadeView(controlsLayout, false);
    }

    void updateUIOnConnection (){
        ContextUtils.fadeView(errorLayout, false);
        ContextUtils.fadeView(controlsLayout, true);
    }

    void startSensorPolling() {
        gyroEngine = new PollingGyroEngine((SensorManager) getSystemService(MainActivity.SENSOR_SERVICE));
        gyroEngine.addListener(obj -> {
            if (obj != null && mouseManager != null) {
                EulerAngles angles = obj.asEulerAngles();
                mouseManager.update(angles);
                float x = mouseManager.getX();
                float y = mouseManager.getY();
                ConcurrencyUtils.runOnUiThread(() -> updateDebugInfo("DEBUG: X: " + x + ", Y: " + y + ", LINEAR-X: " + mouseManager.getOrigX() + ", LINEAR-Y: " + mouseManager.getOrigY()));
                wifiClient.get().write(mouseManager.constructPacket().asPacket());
            }
            return true;
        });
        if(keyboardButton != null) {
            keyboardButton.setKeyboardListener(new VirtualKeyboardButton.KeyboardListener() {
                @Override
                public void onBackspace() {
                    if(wifiClient != null && wifiClient.get() != null) {
                        wifiClient.get().write(new byte[]{11,0,0,0,0,0,0,0,0,0});
                    }
                }

                @Override
                public void onEnter() {
                    if(wifiClient != null && wifiClient.get() != null) {
                        wifiClient.get().write(new byte[]{12,0,0,0,0,0,0,0,0,0});
                    }
                }

                @Override
                public void onKeyPress(char c) {
                    if(wifiClient != null && wifiClient.get() != null) {
                        wifiClient.get().write(new FastKeyPacket(c).asPacket());
                    }
                }
            });
        }
    }

    void stopAll() {
        mouseManager = null;
        if(wifiClient != null && wifiClient.get() != null) {
            try {
                wifiClient.get().disconnect();
            } catch (IOException e) {
                Log.w(Constants.TAG, "Unexpected error closing connection!", e);
            }
            wifiClient.set(null);
        }
        if(gyroEngine != null) {
            gyroEngine.destroy();
            gyroEngine = null;
        }
        if(keyboardButton != null) {
            keyboardButton.setKeyboardListener(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gyroEngine != null) {
            gyroEngine.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gyroEngine != null) {
            gyroEngine.unpause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAll();
    }
}

class ConnectObservable extends Observable<WIFIClient> {

    protected ConnectObservable(String ip) {
        super(new ConnectionOnSubscribe(ip));
    }

    static class ConnectionOnSubscribe implements OnSubscribe<WIFIClient> {

        String ip;

        public ConnectionOnSubscribe(String ip) {
            this.ip = ip;
        }

        @Override
        public void call(Subscriber<? super WIFIClient> subscriber) {
            Log.i(Constants.TAG, "Connect task started!");
            WIFIClient wifiClient = new WIFIClient();
            try {
                wifiClient.connect(ip);
            } catch (IOException e) {
                subscriber.onError(e);
            }
            subscriber.onNext(wifiClient);
            subscriber.onCompleted();
        }
    }
}