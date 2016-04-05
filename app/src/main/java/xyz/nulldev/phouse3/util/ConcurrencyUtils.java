package xyz.nulldev.phouse3.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public class ConcurrencyUtils {
    static Handler handler = null;

    public static void runOnUiThread(Runnable r) {
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            r.run();
        } else {
            if (handler == null) handler = new Handler(Looper.getMainLooper());
            handler.post(r);
        }
    }

    static ScheduledThreadPoolExecutor executorService = null;

    public static ScheduledThreadPoolExecutor initOrGetExecutorService() {
        if (executorService == null) executorService = new ScheduledThreadPoolExecutor(8);
        return executorService;
    }

    public static void runAsync(Runnable r) {
        initOrGetExecutorService().execute(r);
    }
}
