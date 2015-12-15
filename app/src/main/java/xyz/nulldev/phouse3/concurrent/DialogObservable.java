package xyz.nulldev.phouse3.concurrent;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;

import rx.Observable;
import rx.Subscriber;
import xyz.nulldev.phouse3.util.ConcurrencyUtils;

/**
 * Project: Phouse3
 * Created: 13/12/15
 * Author: hc
 */
public class DialogObservable extends Observable<String>{

    public DialogObservable(Context context, String title, String message, String buttonLabel, boolean cancelable) {
        super(new DialogOnSubscribe(context, title, message, null, buttonLabel, cancelable));
    }

    public DialogObservable(Context context, String title, String message, String hint, String buttonLabel, boolean cancelable) {
        super(new DialogOnSubscribe(context, title, message, hint, buttonLabel, cancelable));
    }

    public static class DialogCancelledException extends Exception {}

    static class DialogOnSubscribe implements OnSubscribe<String> {

        Context context;
        String title;
        String message;
        String hint;
        String buttonLabel;
        boolean cancelable;

        public DialogOnSubscribe(Context context, String title, String message, String hint, String buttonLabel, boolean cancelable) {
            this.context = context;
            this.title = title;
            this.message = message;
            this.hint = hint;
            this.buttonLabel = buttonLabel;
            this.cancelable = cancelable;
        }

        @Override
        public void call(Subscriber<? super String> subscriber) {
            ConcurrencyUtils.runOnUiThread(() -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message);
                FrameLayout layout = new FrameLayout(context);
                layout.setPadding(30, 0, 30, 0);
                EditText textView = new EditText(context);
                if (hint != null)
                    textView.setHint(hint);
                layout.addView(textView);
                dialog.setView(layout);
                dialog.setPositiveButton(buttonLabel, (dialog1, which) -> {
                    subscriber.onNext(textView.getText().toString());
                    subscriber.onCompleted();
                });
                if (cancelable) {
                    dialog.setCancelable(true);
                    dialog.setNegativeButton("Cancel", (dialog1, which) -> {
                        dialog1.dismiss();
                        subscriber.onError(new DialogCancelledException());
                        subscriber.onCompleted();
                    });
                    dialog.setOnCancelListener(dialog1 -> {
                        dialog1.dismiss();
                        subscriber.onError(new DialogCancelledException());
                        subscriber.onCompleted();
                    });
                }
                dialog.show();
            });
        }
    }
}
