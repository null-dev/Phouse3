package xyz.nulldev.phouse3.io;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.nulldev.phouse3.Constants;

/**
 * Project: Phouse3
 * Created: 18/11/15
 * Author: nulldev
 */

/**
 * WIFI Client communication using bytes. Please note that ALL packets must be 10 bytes long
 */
public class WIFIClient {
    Socket activeSocket = null;
    String connectedIP = null;
    BufferedInputStream inputStream;
    BufferedOutputStream outputStream;
    WIFIClientIOMonitor writeMonitor;
    WIFIClientIOMonitor readMonitor;
    Thread writeMonitorThread;
    Thread readMonitorThread;

    AtomicBoolean die = new AtomicBoolean(false);

    public boolean connect(String ip) throws IOException {
        Log.i(Constants.TAG, "[WIFIClient] Connecting to ip: " + ip);
        connectedIP = ip;
        die.set(false);
        try {
            activeSocket = new Socket(ip, Constants.PORT);
            inputStream = new BufferedInputStream(activeSocket.getInputStream());
            outputStream = new BufferedOutputStream(activeSocket.getOutputStream());
            writeMonitor = new WIFIClientIOMonitor(this, WIFIClientIOMonitor.Action.WRITE);
            readMonitor = new WIFIClientIOMonitor(this, WIFIClientIOMonitor.Action.READ);
            writeMonitorThread = new Thread(writeMonitor, "Phouse3 > Write Thread");
            writeMonitorThread.start();
            readMonitorThread = new Thread(readMonitor, "Phouse3 > Read Thread");
            readMonitorThread.start();
            write(new byte[]{1,0,0,0,0,0,0,0,0,0});
        } catch (IOException e) {
            Log.i(Constants.TAG, "[WIFIClient] Connection error!", e);
            throw e;
        }
        Log.i(Constants.TAG, "[WIFIClient] Connection established!");
        return true;
    }

    public void disconnect() throws IOException {
        if(activeSocket != null) {
            Log.i(Constants.TAG, "[WIFIClient] Disconnecting...");
            try {
                writeMonitor.toWrite.clear();
                writeMonitor.blockingWrite(new byte[]{0,0,0,0,0,0,0,0,0,0});
            } catch(IOException ignored) {}
            die.set(true);
            try {
                writeMonitorThread.interrupt();
                readMonitorThread.interrupt();
                activeSocket.close();
                inputStream = null;
                outputStream = null;
                connectedIP = null;
                activeSocket = null;
                writeMonitor = null;
                readMonitor = null;
                writeMonitorThread = null;
                readMonitorThread = null;
            } catch (IOException e) {
                Log.i(Constants.TAG, "[WIFIClient] Disconnect error!", e);
                throw e;
            }
        }
    }

    public void write(byte[] data) {
        if(activeSocket != null) {
            writeMonitor.doWrite(data);
        }
    }

    public WIFIClientIOMonitor.ReadListener getReadListener() {
        return readMonitor.getReadListener();
    }

    public void setReadListener(WIFIClientIOMonitor.ReadListener readListener) {
        readMonitor.setReadListener(readListener);
    }

    public Socket getActiveSocket() {
        return activeSocket;
    }

    public void setActiveSocket(Socket activeSocket) {
        this.activeSocket = activeSocket;
    }

    public String getConnectedIP() {
        return connectedIP;
    }

    public void setConnectedIP(String connectedIP) {
        this.connectedIP = connectedIP;
    }

    public BufferedInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public BufferedOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(BufferedOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public WIFIClientIOMonitor getWriteMonitor() {
        return writeMonitor;
    }

    public void setWriteMonitor(WIFIClientIOMonitor writeMonitor) {
        this.writeMonitor = writeMonitor;
    }

    public WIFIClientIOMonitor getReadMonitor() {
        return readMonitor;
    }

    public void setReadMonitor(WIFIClientIOMonitor readMonitor) {
        this.readMonitor = readMonitor;
    }

    public AtomicBoolean getDie() {
        return die;
    }

    public void setDie(AtomicBoolean die) {
        this.die = die;
    }
}
