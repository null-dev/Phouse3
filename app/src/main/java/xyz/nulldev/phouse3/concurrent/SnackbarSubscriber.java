package xyz.nulldev.phouse3.concurrent;

import android.view.View;

import rx.Subscriber;
import xyz.nulldev.phouse3.util.ContextUtils;

/**
 * Created: 13/12/15
 * Author: hc
 */
public class SnackbarSubscriber extends Subscriber {

    View view;
    String message;
    int length;

    public SnackbarSubscriber(View view, String message, int length) {
        this.view = view;
        this.message = message;
        this.length = length;
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {}

    @Override
    public void onNext(Object o) {
        ContextUtils.doSnackbar(view, message, length);
    }
}
