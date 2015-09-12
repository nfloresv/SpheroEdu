package cl.flores.nicolas.spheroedu.interfaces;

import java.net.Socket;

/**
 * Created by Nicolas on 12/09/15.
 */
public interface SocketInterface {

    /**
     * Receive a Socket so the class can connect with Sphero
     * @param socket the connection with Sphero
     */
    public void setSocket(Socket socket);
}
