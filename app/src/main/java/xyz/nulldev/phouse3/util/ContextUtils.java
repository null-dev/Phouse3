package xyz.nulldev.phouse3.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.NotificationManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public class ContextUtils {
    public static void dismissNotification(Context context, int notificationID) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(notificationID);
    }
    public static void doToast(final Context context, final String text, final int length) {
        ConcurrencyUtils.runOnUiThread(() -> Toast.makeText(context, text, length).show());
    }

    public static void doToast(final Context context, final int text, final int length) {
        ConcurrencyUtils.runOnUiThread(() -> Toast.makeText(context, text, length).show());
    }

    public static void doSnackbar(View view, String message, int length) {
        ConcurrencyUtils.runOnUiThread(() -> Snackbar.make(view, message, length).show());
    }

    public static void doSnackbar(View view, int message, int length) {
        ConcurrencyUtils.runOnUiThread(() -> Snackbar.make(view, message, length).show());
    }

    public static void fadeView(final View view, final boolean show) {
        int shortAnimTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        view.setVisibility(show ? View.VISIBLE : View.GONE);
        view.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
