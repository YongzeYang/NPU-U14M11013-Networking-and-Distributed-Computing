package exec1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * 文件传输的客户端。
 *
 * @author Yang Yongze
 */
public class FileClient {

    Socket socket;//客户端套接字，通过TCP连接至服务器端
    String HOST = "127.0.0.1";//服务器端的IP地址，为本机。
    //在连接至服务器时，操作系统会自动分配端口号，因此不用在服务器端指定PORT。
    final int TCP_PORT = 20210;//服务器端的TCP端口号，而非客户端。
    final int UDP_PORT = 2023;//服务器端的UDP端口号

    DatagramSocket datagramSocket;//接收数据包的Socket
    FileOutputStream fileOutputStream;//文件输出流

    final int KB = 1024;//定义一个千字节的字节大小

    /**
     * Construction function.
     */
    public FileClient() {
        try {
            socket = new Socket(HOST, TCP_PORT);//连接服务器端的服务器
            System.out.println("已连接到服务器:[" +HOST+":"+TCP_PORT+"]。");
            System.out.println("该服务器用于实现文件的下载。用法：");
            System.out.println("ls              列出当前目录名单。");
            System.out.println("cd <dir>        跳转到<dir>");
            System.out.println("cd..            返回上一级目录。");
            System.out.println("get <filename>  下载文件<filename>");
            System.out.println("请输入命令:");
        } catch (IOException e) {
            System.out.println("错误：服务器:[" +HOST+":"+TCP_PORT+"]未开启或连接失败！");

        }
    }

    /**
     * 客户端运行的具体方法。
     */
    public void run() {
        try {
            //客户端输出流，向服务器发消息
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
            //客户端输入流，接收服务器消息
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter pw = new PrintWriter(bw, true); //装饰输出流，及时刷新

            Scanner in = new Scanner(System.in); //接受用户信息
            String msg = null;//用户输入的指令
            while ((msg = in.nextLine()) != null) {
                //使用StringTokenizer将消息分隔为命令与参数。
                StringTokenizer stringTokenizer = new StringTokenizer(msg);
                String command = null;
                if(stringTokenizer.hasMoreTokens()) {
                    command = stringTokenizer.nextToken();
                String dir = null;
                if(stringTokenizer.hasMoreTokens())
                    dir = stringTokenizer.nextToken();

                pw.println(msg); //将整条指令发送给服务器端
                if(command.equals("get")){
                    int fileLength =0;
                    if(dir!=null){
                            fileLength =Integer.parseInt(br.readLine());//从输入流获取服务器端发来的文件大小消息，详见Handler类166行。
                            getFile(dir,fileLength);//将文件名和长度大小信息传到getFile方法。
                    } else System.out.println("缺少参数。get用法：\nget <filename>  下载文件<filename>");
                }

                String out = null;//接收服务器端发来的其他消息
                while ((out = br.readLine())!=null) {
                    if(out.equals("END")) break;//当接收到END时，结束本次会话。详见Handler类switch中每个case的最后一句
                    System.out.println(out);
                }
                System.out.println();

                if (msg.equals("bye")) {
                    System.out.println("客户端已退出连接。");
                    break; //退出
                }}
                else {
                    System.out.println("用法：");
                    System.out.println("ls              列出当前目录名单。");
                    System.out.println("cd <dir>        跳转到<dir>");
                    System.out.println("cd..            返回上一级目录。");
                    System.out.println("get <filename>  下载文件<filename>");
                    System.out.println("请输入命令:");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != socket) {
                try {
                    socket.close(); //断开连接
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件。
     * 获取到的文件默认放在工作区目录中。
     * @param file 文件名称
     * @param length 文件大小
     */
    private void getFile(String file, int length) {
        try {
            DatagramPacket dp = new DatagramPacket(new byte[KB], KB);//用于接收UDP数据
            datagramSocket = new DatagramSocket(UDP_PORT);//创建数据报Socket并绑定到指定的UDP端口
            fileOutputStream = new FileOutputStream(new File(file));//将文件作为File输出流
            int packetNum = length / KB;//统计要发送的数据包数量
            if (length % KB != 0) packetNum++;//如果不能除尽，那就多一个数据包
            System.out.println("正在接受文件。数据包大小："+packetNum+"KB。");
            for (int i = 0; i < packetNum; i++) {//对于每一个数据包
                datagramSocket.receive(dp);//通过Socket获取数据包
                fileOutputStream.write(dp.getData(), 0, dp.getLength());//将这个包获取的字节作为文件输出流输出
                fileOutputStream.flush();//刷新缓冲的数据流，将该流的所有输出数据写出
            }
            fileOutputStream.close();//关闭文件流

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            datagramSocket.close();
        }
    }

    /**
     * 开启客户端。
     * Idea中开启多线程的方法：运行->编辑配置->修改选项->允许多个实例
     * @param args 客户端不用传入参数。
     */
    public static void main(String[] args) {
        FileClient fileClient = new FileClient();
        fileClient.run();
    }

}
