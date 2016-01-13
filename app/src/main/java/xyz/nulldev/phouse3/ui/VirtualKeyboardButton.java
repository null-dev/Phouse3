package xyz.nulldev.phouse3.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import xyz.nulldev.phouse3.Constants;

/**
 * Project: Phouse3
 * Created: 15/12/15
 * Author: hc
 */
public class VirtualKeyboardButton extends Button {

    KeyboardListener keyboardListener = null;

    public void setup() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnClickListener(v -> {
            Log.i(Constants.TAG, "Forcing keyboard open...");
            InputMethodManager mgr = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            requestFocus();
            mgr.showSoftInput(VirtualKeyboardButton.this, InputMethodManager.SHOW_FORCED);
        });
    }

    public VirtualKeyboardButton(Context context) {
        super(context);
        setup();
    }

    public VirtualKeyboardButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public VirtualKeyboardButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyboardListener != null) {
            int keycode = event.getKeyCode();
            if (keycode == 67) {
                //Backspace
                keyboardListener.onBackspace();
            } else if (keycode == 66) {
                //Enter
                keyboardListener.onEnter();
            } else {
                int keyunicode = event.getUnicodeChar(event.getMetaState());
                char character = (char) keyunicode;
                keyboardListener.onKeyPress(character);
            }
            return true;
        }
        return false;
    }

    public KeyboardListener getKeyboardListener() {
        return keyboardListener;
    }

    public void setKeyboardListener(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }

    public interface KeyboardListener {
        void onBackspace();
        void onEnter();
        void onKeyPress(char c);
    }
}
