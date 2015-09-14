package cl.flores.nicolas.spheroedu.threads;

import cl.flores.nicolas.spheroedu.interfaces.SocketInterface;

public class ServerBluetoothThread extends Thread {
    private SocketInterface socketInterface;

    public ServerBluetoothThread(SocketInterface socketInterface) {
        this.socketInterface = socketInterface;
    }

    public void run() {
    }

    public void cancel() {
    }
}
