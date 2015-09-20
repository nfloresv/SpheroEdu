package cl.flores.nicolas.spheroedu.interfaces;

import android.bluetooth.BluetoothSocket;

public interface SocketInterface {

    /**
     * Receive a Socket so the class can connect with Sphero
     *
     * @param socket the connection with Sphero
     */
    void setSocket(BluetoothSocket socket);
}
