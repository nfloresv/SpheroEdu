package cl.flores.nicolas.spheroedu.threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;

public class ClientBluetoothThread extends Thread {
    private final BluetoothSocket socket;
    private final SocketInterface socketInterface;


    public ClientBluetoothThread(BluetoothDevice device, SocketInterface socketInterface) {
        this.socketInterface = socketInterface;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(Constants.APPLICATION_UUID));
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error creating client socket", e);
        }
        socket = tmp;
    }

    public void run() {
        try {
            socket.connect();
        } catch (IOException connectException) {
            Log.e(Constants.LOG_TAG, "Error connecting as client", connectException);
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(Constants.LOG_TAG, "Error closing client connection", closeException);
            }
            return;
        }
        socketInterface.setSocket(socket);
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error canceling client connection", e);
        }
    }
}
