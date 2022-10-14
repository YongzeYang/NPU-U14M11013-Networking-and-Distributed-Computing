package exec2;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP服务器端。
 * 超文本传输协议（HTTP）使用传输控制协议（TCP）以进行可靠传输。
 * 类似于exec1的文件系统服务器，通过建立服务器端套接字开启TCP连接即可。
 *
 * @author Yongze Yang
 * @date 2021-10-6
 */
public class HttpServer {
    ServerSocket serverSocket;       // 服务器端套接字
    ExecutorService executorService; //线程池

    final String HOST = "127.0.0.1"; //建立本地服务器
    //final int TCP_PORT = 2021;     // TCP端口号
    final int HTTP_PORT = 80;        // HTTP端口号
    final int BACKLOG = 10;          // 设定客户端连接请求的队列长度
    final int POOL_SIZE = 4;         // 线程池大小

    /**
     * 构造函数。
     * 建立TCP连接，启动线程池。
     */
    public HttpServer() {
        try {
            serverSocket = new ServerSocket(HTTP_PORT,BACKLOG); //建立TCP连接.
            System.out.println("服务器已建立. IP地址:[" + HOST + "], 端口号:[" + HTTP_PORT +"].");
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动Server服务。
     * @param path 根目录
     */
    public void service(String path) {
        System.out.println("根目录: [" + path + "]. ");
        while(true) {  //需要一直启动监听
            Socket threadSocket;
            try {
                threadSocket = serverSocket.accept();//从队列中取出连接请求
                Thread work = new Thread(new HttpHandler(threadSocket, path));//启动线程，由Handler类执行具体的操作
                work.start();//启动线程
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main方法，启动服务器，根据启动参数打开根目录。
     * Idea中设定启动参数的方法：运行->编辑配置->在构建和运行中选中HttpServer类->输入运行参数
     * @param args 须传递一个启动参数，参数为启动文件服务的根目录。
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("用法: java HttpServer <目录>");
        } else {
            File root = new File(args[0]);
            if(root.isDirectory()) {  //判定传入的启动参数是否有效
                new HttpServer().service(args[0]); //启动服务器
            } else {
                System.out.println("错误:目录 <" + args[0] + "> 不存在.");
            }
        }

    }

}
