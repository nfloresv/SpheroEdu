package cl.flores.nicolas.spheroedu.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import cl.flores.nicolas.spheroedu.Utils.Constants;
import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;

public class ServerBluetoothThread extends Thread {
    private final BluetoothServerSocket bluetoothServerSocket;
    private final SocketInterface socketInterface;

    public ServerBluetoothThread(SocketInterface socketInterface, String appName) {
        this.socketInterface = socketInterface;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, UUID.fromString(Constants.APPLICATION_UUID));
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error getting server socket", e);
        }
        bluetoothServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Error getting server connection", e);
                break;
            }
            if (socket != null) {
                try {
                    bluetoothServerSocket.close();
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "Error closing server connection", e);
                } finally {
                    socketInterface.setSocket(socket);
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error canceling server connection", e);
        }
    }
}
