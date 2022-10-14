package exam1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Proxy服务器端。
 * Web代理服务器监听客户端的request请求，将request转发到目标HTTP服务器，并将响应的文件转发到客户端。
 *
 * @author Yongze Yang
 * @date 2021-10-8
 */
public class ProxyServer {
    ServerSocket serverSocket;       // 服务器端套接字
    ExecutorService executorService; //线程池

    final String HOST = "127.0.0.1"; //建立本地服务器
    final int Proxy_PORT = 8000;        // HTTP端口号
    final int BACKLOG = 10;          // 设定客户端连接请求的队列长度
    final int POOL_SIZE = 4;         // 线程池大小

    /**
     * 构造函数。
     * 建立TCP连接，启动线程池。
     */
    public ProxyServer() {
        try {
            serverSocket = new ServerSocket(Proxy_PORT,BACKLOG); //建立TCP连接.
            System.out.println("代理服务器已建立. IP地址:[" + HOST + "], 端口号:[" + Proxy_PORT +"].");
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动Server服务。
     */
    public void service() {
        while(true) {  //需要一直启动监听
            Socket threadSocket;
            try {
                threadSocket = serverSocket.accept();//从队列中取出连接请求
                Thread work = new Thread(new ProxyHandler(threadSocket));//启动线程，由Handler类执行具体的操作
                work.start();//启动线程
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main方法，启动服务器。
     * @param args 忽略，不需要启动参数。
     */
    public static void main(String[] args) {
        new ProxyServer().service(); //启动服务器
    }

}
