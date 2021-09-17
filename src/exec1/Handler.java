package exec1;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * 该类为每个线程实现具体的Socket操作。
 * @author Yang Yongze
 */
public class Handler implements Runnable {
    private final Socket socket;
    private final String rootPath; //根目录
    private String currentPath;  //当前目录
    BufferedReader br;
    BufferedWriter bw;
    PrintWriter pw;
    DatagramSocket datagramSocket;
    SocketAddress socketAddress;

    static final int UDP_PORT = 2023;
    static final String HOST = "127.0.0.1";
    static final int SIZE = 1024;

    /**
     * 构造函数。
     * @param socket 套接字
     * @param path 根目录
     */
    public Handler(Socket socket, String path) {
        this.socket = socket;
        this.currentPath = path;
        this.rootPath = path;
    }

    /**
     * 初始化输入与输出流。
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

    /**
     * 启动线程的具体方法。
     */
    @Override
    public void run() {
        try {
            System.out.println("新客户端连接。 IP地址: [" + socket.getInetAddress() + ":"
                    + socket.getPort() + "]"); //客户端信息
            initStream(); // 初始化输入输出流对象
            String info;//用户每行输入的信息。

            while (null != (info = br.readLine())) {//通过buffered reader读取用户每行输入的信息。
                if (info.equals("bye")) { // 如果用户输入“bye”就退出
                    System.out.println("客户端[" + socket.getInetAddress() + ":"
                            + socket.getPort() + "]"+"已断开连接。");
                    break;
                }

                boolean valid = true;//该变量判断参数是否有效，初始为true。

                StringTokenizer stringTokenizer = new StringTokenizer(info);//创立分隔符，将info分隔成命令与参数。
                String command = stringTokenizer.nextToken();
                String dir = null;

                System.out.print("客户端[" + socket.getInetAddress() + ":"
                        + socket.getPort() + "]"+"输入命令：" + command);

                if(stringTokenizer.hasMoreTokens()) {
                    dir = stringTokenizer.nextToken();
                    System.out.print("  参数:<"+dir+">");
                    File fil = new File(currentPath+"\\"+dir);
                    if(!fil.isDirectory()&&!fil.isFile()) { //参数既不是目录，又不是文件。
                        System.out.print("\n注意：路径 <" + currentPath + "\\" + dir + "> 不存在或无效！");
                        valid = false;//如果路径无效，则设定判断参数为false。
                    }
                    if(stringTokenizer.hasMoreTokens()) {
                        valid = false;//如果输入多个参数，那么判定参数为false。
                    }
                }
                System.out.println();

                if(valid)
                    switch(command) {
                        case "ls" ://显示当前目录下的文件信息
                            showDirectory(currentPath);
                            pw.println("当前路径："+currentPath);
                            pw.println("END");//用于终止，客户端读取到END后终止读取服务器发送的消息。详见FileClient类81行。
                            break;
                        case "cd" :
                            if(dir==null) { //用户只传入了一个参数。
                                pw.println("缺少参数，请重新输入。用法：");
                                pw.println("cd <dir>        跳转到<dir>");
                                pw.println("当前路径："+currentPath);
                                pw.println("END");
                                break;
                            } else if (dir.equals("..")) { //如果参数为..
                                jumpToDirectory();//跳转回当前路径
                                pw.println("当前路径："+currentPath);
                                pw.println("END");
                                break;
                            }
                            jumpToDirectory(dir);//跳转路径。
                            pw.println("当前路径："+currentPath);
                            pw.println("END");
                            break;

                        case "cd.." :
                            jumpToDirectory();//跳转回上一级目录
                            pw.println("当前路径："+currentPath);
                            pw.println("END");
                            break;
                        case "get" ://获取文件
                            if(dir!=null)
                            sendFile(currentPath+"\\"+dir);//传入文件的绝对目录
                            pw.println("当前路径："+currentPath);
                            pw.println("END");
                            break;
                        default :
                            pw.println("未知命令。用法：");//其他命令为未知命令。输出命令的用法。
                            pw.println("ls              列出当前目录名单。");
                            pw.println("cd <dir>        跳转到<dir>");
                            pw.println("cd..            返回上一级目录。");
                            pw.println("get <filename>  获取文件<filename>");
                            pw.println("当前路径："+currentPath);
                            pw.println("END");
                            break;
                    }
                else { //参数无效，则返回以下信息。
                    pw.println("参数<"+dir+">无效！找不到文件或路径<"+currentPath+"\\"+dir+">");
                    pw.println("当前路径："+currentPath);
                    pw.println("END");
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

    /**
     * 将当前路径下的文件信息输出。
     * @param file 文件路径
     */
    private void sendFile(String file) {
        File currentFile = new File(file);//实例化一个文件对象，绝对路径为File。

        if(currentFile.isDirectory()){
            pw.println("<"+file+">是一个目录，而不是文件！");
        } else {
            pw.println(currentFile.length());//将文件的大小发送到客户端。详见FileClient类75行。
            try {
                datagramSocket = new DatagramSocket();//创建服务器端数据包套接字
                socketAddress = new InetSocketAddress(HOST,UDP_PORT);//通过UDP建立连接。
                DatagramPacket datagramPacket;//用于发送数据的数据包

                byte[] sendInfo = new byte[SIZE];
                int size = 0;
                datagramPacket = new DatagramPacket(sendInfo,sendInfo.length,socketAddress);//实例化包的对象，长度为1K的字节组

                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));//通过读取文件输入流获取输入流

                while ((size = bufferedInputStream.read(sendInfo)) > 0) {//size为读取1K字节时实际读取到的字节数量
                    datagramPacket.setData(sendInfo);//设定缓冲区
                    datagramSocket.send(datagramPacket);//从此套接字发送数据包
                    sendInfo = new byte[SIZE];//重新设置发送的信息
                    TimeUnit.MICROSECONDS.sleep(1);
                }
                bufferedInputStream.close();//关闭输入流
                datagramSocket.close();//关闭数据包套接字
                pw.println("文件接收完毕。");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 显示当前目录下的文件信息。
     * @param dir 文件目录
     */
    private void showDirectory(String dir) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
        for(File file:files) {

            if(file.isDirectory()) {
                pw.print("<dir>         "+file.getName() +"    ");
                int i = 20-file.getName().length();
                if(i>1)for(int j=0;j<i;j++)pw.print(" ");else pw.print("  ");//使输出对齐，并考虑文件名太长的情况
                pw.print(file.length()/1000+"KB"+"\n");

            } else if(file.isFile()) {
                pw.print("<file>         "+file.getName() +"    ");
                int i = 20-file.getName().length();
                if(i>1)for(int j=0;j<i;j++)pw.print(" ");else pw.print("  ");
                pw.print(file.length()/1000+"KB"+"\n");
            }
        }
        if (files.length==0) {
            pw.println("该目录为空文件夹。");
        }
    }

    /**
     * 跳转到目录。
     * @param dir 要跳转的目录
     */
    private void jumpToDirectory(String dir) {
        File file = new File(currentPath + "\\" +dir);
        if(file.isFile()){ //由于之前已判定过dir是否有效，所以在这里只用判定是不是文件即可
            pw.println("参数<"+dir+">是一个文件而不是目录。请重新输入。");
        } else
            currentPath = currentPath + "\\" + dir;
    }

    /**
     * 跳转到上一级目录。
     */
    private void jumpToDirectory() {
        if(currentPath.equals(rootPath)){
            pw.println("该目录为根目录，无法继续跳转至上一级。");
        } else {
            String path = "";
            StringTokenizer stringTokenizer = new StringTokenizer(currentPath,"\\");
            //这里用tokenizer是因为String的spilt方法认为"\\"不是合法的转义符
            int count = stringTokenizer.countTokens();
            //返回上一级目录，只需返回上以及目录即可，如D://A/B/C只需返回D://A/B
            for(int i=0;i<count-2;i++) {
                path = path + stringTokenizer.nextToken();
                path += "\\";
            }
            path = path+stringTokenizer.nextToken();
            currentPath = path;
            pw.println("已跳转至上一级目录。");
        }

    }
}
