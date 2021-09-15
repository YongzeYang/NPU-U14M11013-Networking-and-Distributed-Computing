package exec1;

import java.io.IOException;
import java.net.Socket;

/**
 * File Client.
 *
 * @author Yang Yongze
 */
public class FileClient {

    Socket socket;

    String HOST = "127.0.0.1";//Server IP. Localhost.
    int TCP_PORT = 2021;
    int UDP_PORT = 2020;

    /**
     * Construction function.
     */
    public FileClient() {
        try {
            socket = new Socket(HOST,TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
