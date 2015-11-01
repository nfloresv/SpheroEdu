package cl.flores.nicolas.spheroedu.threads;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cl.flores.nicolas.spheroedu.Utils.Constants;

public class CommunicationThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;
    private final Handler mHandler;

    public CommunicationThread(BluetoothSocket socket, Handler mHandler) {
        this.socket = socket;
        this.mHandler = mHandler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error creating streams", e);
        }

        inStream = tmpIn;
        outStream = tmpOut;
    }

    public void run() {
        // TODO verificar tamaño del buffer, puede ser pequeño para json
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inStream.read(buffer);
                String message = new String(buffer, 0, bytes, Charsets.UTF_8);
                mHandler.obtainMessage(Constants.MESSAGE_SEND, message).sendToTarget();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Error reading stream", e);
                break;
            }
        }
    }

    public void write(String message) {
        try {
            outStream.write(message.getBytes(Charsets.UTF_8));
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error writing stream", e);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error closing stream", e);
        }
    }
}
