package cl.flores.nicolas.spheroedu.Utils;

import android.bluetooth.BluetoothSocket;

import com.orbotix.ConvenienceRobot;

import java.util.ArrayList;

/**
 * Class to transfer socket and robots
 */
public class CommunicationManager {
    private static CommunicationManager manager;
    private final ArrayList<BluetoothSocket> sockets;
    private final ArrayList<ConvenienceRobot> robots;

    private CommunicationManager() {
        sockets = new ArrayList<>();
        robots = new ArrayList<>();
    }

    public static CommunicationManager getInstance() {
        if (manager != null) {
            manager = new CommunicationManager();
        }
        return manager;
    }

    public void putSocket(BluetoothSocket socket) {
        sockets.add(socket);
    }

    public void putRobot(ConvenienceRobot robot) {
        robots.add(robot);
    }

    public ArrayList<ConvenienceRobot> getRobots() {
        return robots;
    }

    public ArrayList<BluetoothSocket> getSockets() {
        return sockets;
    }

    public BluetoothSocket getSocket() {
        return sockets.get(0);
    }
}
