package xyz.nulldev.phouse3.concurrent;

import android.view.View;

import rx.functions.Func1;
import xyz.nulldev.phouse3.util.ContextUtils;

/**
 * Created: 13/12/15
 * Author: hc
 */
public class SnackbarFunc<T> implements Func1<T, T> {

    View view;
    String message;
    int length;

    public SnackbarFunc(View view, String message, int length) {
        this.view = view;
        this.message = message;
        this.length = length;
    }

    @Override
    public T call(T o) {
        ContextUtils.doSnackbar(view, message, length);
        return o;
    }
}
