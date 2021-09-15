package exec1;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Yang Yongze
 */
public class FileServer {
    ServerSocket serverSocket; // TCP Socket.
    ExecutorService executorService; //Thread pool.

    final String HOST = "127.0.0.1"; //Localhost.
    final int TCP_PORT = 2021;
    final int BACKLOG = 10;
    final int POOL_SIZE = 4;

    public FileServer() {
        try {
            serverSocket = new ServerSocket(TCP_PORT,BACKLOG); //Create TCP connection.
            System.out.println("Server started successfully. IP:[" + HOST + "], PORT:[" + TCP_PORT +"].");
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starting service.
     * @param path File directory.
     */
    public void service(String path) {
        System.out.println("Root path: [" + path + "]. ");
        while(true) {
            Socket threadSocket;
            try {
                threadSocket = serverSocket.accept();
                Thread work = new Thread(new Handler(threadSocket, path));
                work.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Server starts here.
     * @param args Root directory.
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java FileServer <dir>");
        } else {
            // Root path.
            File root = new File(args[0]);
            if(root.isDirectory()) {
                new FileServer().service(args[0]);
            } else {
                System.out.println("Error: <" + args[0] + "> is not a directory or does not exist.");
            }
        }

    }
}
