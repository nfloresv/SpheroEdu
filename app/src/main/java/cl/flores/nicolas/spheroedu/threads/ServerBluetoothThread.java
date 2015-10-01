package cl.flores.nicolas.spheroedu.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;

public class ServerBluetoothThread extends Thread {
    private final BluetoothServerSocket bluetoothServerSocket;
    private final SocketInterface socketInterface;
    private final String appName;

    public ServerBluetoothThread(SocketInterface socketInterface, String appName, String uuid) {
        this.socketInterface = socketInterface;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.appName = appName;
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, UUID.fromString(uuid));
        } catch (IOException e) {
            Log.e(appName, "Error getting connection", e);
        }
        bluetoothServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                Log.e(appName, "Error accepting connection", e);
                break;
            }
            if (socket != null) {
                try {
                    bluetoothServerSocket.close();
                } catch (IOException e) {
                    Log.e(appName, "Error closing connection", e);
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
            Log.e(appName, "Error closing connection", e);
        }
    }
}
