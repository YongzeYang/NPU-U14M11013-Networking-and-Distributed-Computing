package exec1;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * This class handles socket workings from each thread.
 */
public class Handler implements Runnable {
    private Socket socket;
    private File root;
    private String currentPath;
    private String rootPath;
    BufferedReader br;
    BufferedWriter bw;
    PrintWriter pw;

    public Handler(Socket socket, String path) {
        this.socket = socket;
        this.currentPath = path;
        this.rootPath = path;
    }

    /**
     * Init input and output stream.
     */
    public void initStream() { // 初始化输入输出流对象方法
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            pw = new PrintWriter(bw, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("New client. IP address: [" + socket.getInetAddress() + "："
                    + socket.getPort() + "]"); //客户端信息
            initStream(); // 初始化输入输出流对象
            String info;
            while (null != (info = br.readLine())) {
                if (info.equals("bye")) { // 如果用户输入“bye”就退出
                    System.out.println("Disconnect successfully.");
                    break;
                }

                StringTokenizer stringTokenizer = new StringTokenizer(info);
                String command = stringTokenizer.nextToken();
                String dir = null;
                if(stringTokenizer.hasMoreTokens()) {
                    dir = stringTokenizer.nextToken();
                }

                switch(command) {
                    case "ls" :
                        showDirectory(currentPath);
                        break;
                    case "cd" :

                        jumpToDirectory(dir);
                        break;
                    case "ls.." :

                        break;
                    case "get" :
                        break;
                    default :
                        System.out.println("Unknow command.");
                        break;
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendFile(String file) {
        File file1 = new File(file);

    }

    private void showDirectory(String dir) {

    }

    private void jumpToDirectory(String dir) {

    }

}
