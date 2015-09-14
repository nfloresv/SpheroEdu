package cl.flores.nicolas.spheroedu.interfaces;

import java.net.Socket;

public interface SocketInterface {

    /**
     * Receive a Socket so the class can connect with Sphero
     *
     * @param socket the connection with Sphero
     */
    void setSocket(Socket socket);
}
