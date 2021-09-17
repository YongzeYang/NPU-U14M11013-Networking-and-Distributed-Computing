package exec1;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 该类在服务器端通过TCP建立Socket服务器。
 * 针对每个线程的具体的Socket操作详见Handler类。
 * 该类运行在服务器端。
 * @author Yang Yongze
 */
public class FileServer {
    ServerSocket serverSocket; // 服务器端套接字.
    ExecutorService executorService; //线程池

    final String HOST = "127.0.0.1"; //建立本地服务器
    final int TCP_PORT = 20210; // TCP端口号，可以自定义，但要注意冲突。
    final int BACKLOG = 10;//设定客户端连接请求的队列长度
    final int POOL_SIZE = 4;//线程池大小

    /**
     * 构造函数。
     * 建立TCP连接，启动线程池。
     */
    public FileServer() {
        try {
            serverSocket = new ServerSocket(TCP_PORT,BACKLOG); //建立TCP连接.
            System.out.println("服务器已建立. IP地址:[" + HOST + "], 端口号:[" + TCP_PORT +"].");
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
                Thread work = new Thread(new Handler(threadSocket, path));//启动线程，由Handler类执行具体的操作
                work.start();//启动线程
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Main方法，启动服务器，根据启动参数打开根目录。
     * Idea中设定启动参数的方法：运行->编辑配置->在构建和运行中选中FileServer类->输入运行参数
     * @param args 须传递一个启动参数，参数为启动文件服务的根目录。
     */
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("用法: java FileServer <目录>");
        } else {
            File root = new File(args[0]);
            if(root.isDirectory()) {  //判定传入的启动参数是否有效
                new FileServer().service(args[0]); //启动服务器
            } else {
                System.out.println("错误:目录 <" + args[0] + "> 不存在.");
            }
        }

    }
}
