package xyz.nulldev.phouse3.io;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import xyz.nulldev.phouse3.Constants;
import xyz.nulldev.phouse3.util.Utils;

/**
 * Project: Phouse3
 * Created: 18/11/15
 * Author: nulldev
 */
public class WIFIClientIOMonitor implements Runnable {
    WIFIClient client;
    Action action;
    LinkedBlockingQueue<Byte[]> toWrite;
    ReadListener readListener;

    public WIFIClientIOMonitor(WIFIClient client, Action action) {
        this.client = client;
        this.action = action;
        if(action.equals(Action.WRITE)) {
            toWrite = new LinkedBlockingQueue<>();
        }
    }

    @Override
    public void run() {
        while(!client.getDie().get()) {
            try {
                if(action.equals(Action.READ)) {
                    byte[] buffer = new byte[10];
                    client.getInputStream().read(buffer);
                    handleRead(buffer);
                } else if (action.equals(Action.WRITE)) {
                    if(toWrite.size() > 2) {
                        Log.w(Constants.TAG, "[WCIOM] Buffer overflowing, removing all queued messages...");
                        toWrite.clear();
                    }
                    if(!toWrite.isEmpty()) {
                        client.getOutputStream().write(Utils.unbox(toWrite.poll()));
                        client.getOutputStream().flush();
                    }
                }
                //Sleep a bit to prevent wasting the CPU
//                try {Thread.sleep(100);} catch(InterruptedException ignored) {return;}
            } catch(Throwable t) {
                Log.e(Constants.TAG, "[WCIOM] Unhandled exception, ignoring!", t);
            }
        }
    }

    void handleRead(byte[] bytes) {
        if(readListener != null) {
            readListener.read(bytes);
        }
    }

    public void doWrite(byte[] data) {
        try {
            toWrite.put(Utils.box(data));
        } catch (InterruptedException ignored) {}
    }

    public void blockingWrite(byte[] data) throws IOException {
        client.getOutputStream().flush();
        client.getOutputStream().write(data);
        client.getOutputStream().flush();
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        READ, WRITE
    }

    public ReadListener getReadListener() {
        return readListener;
    }

    public void setReadListener(ReadListener readListener) {
        if(action != Action.READ)
            throw new IllegalStateException("Cannot set read listener on write monitor!");
        this.readListener = readListener;
    }

    public interface ReadListener {
        void read(byte[] read);
    }
}