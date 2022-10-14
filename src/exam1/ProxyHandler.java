package exam1;

import java.io.*;
import java.net.Socket;
import java.net.URL;

/**
 * 该类具体实现代理服务器具体线程的功能。
 * 该类仅支持HTTP1.0。
 */
public class ProxyHandler implements Runnable{

    static final String HOST = "127.0.0.1";
    static final int PORT = 8000;
    Socket socket;
    BufferedReader br;
    BufferedWriter bw;
    PrintWriter pw;
    BufferedInputStream bis;
    BufferedOutputStream bos;
    private byte[] buffer;
    private static int buffer_size = 8192;

    {
        buffer = new byte[buffer_size];
    }

    /**
     * Init
     */
    public void initStream() { // 初始化输入输出流对象方法
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            pw = new PrintWriter(bw, true);
            bos = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProxyHandler(Socket threadSocket) {
        this.socket = threadSocket;
    }

    @Override
    public void run() {
        try {
            System.out.println("新客户端连接。 IP地址: [" + socket.getInetAddress() + ":"
                    + socket.getPort() + "]"); //客户端信息
            initStream();

            //String request = br.readLine();
            String[] request;

            String info=br.readLine();
            System.out.print("客户端[" + socket.getInetAddress() + ":" +
                                        + socket.getPort() + "]发送HTTP请求。  ");
            if (null != info) {//通过buffered reader读取用户每行输入的信息。
                request = info.split(" ");
            } else
                return;

            if(request.length==3) {
                System.out.println("请求类型：["+request[0]+"] 请求地址:" +request[1]+" HTTP类型:"+request[2]);
            } else System.out.println("请求无效！");
            URL url = new URL(request[1]);
            String send = request[0] +" " + url.getPath() +" " + request[2];
            System.out.println(send);

            if(request[0].equals("GET")&&request[2].equals("HTTP/1.0")){
                System.out.println("正在连接"+url.toString());
                ProxyClient proxyClient = new ProxyClient();
                if(url.getPort()==-1)proxyClient.connect(url.getHost());
                else proxyClient.connect(url.getHost(),url.getPort());
                System.out.println("连接成功。正在发送请求。");

                proxyClient.processGetRequest(send,url.getHost());
                System.out.println();
                proxyClient.close();
                System.out.println("Header: \n");
                System.out.println(proxyClient.getHeader() + "\n");
                System.out.println();
                System.out.println("response:");
                String response = proxyClient.getResponse();
                System.out.println(response);

                bos.write(buffer, 0, proxyClient.getHeader().length());
                bos.write(response.getBytes());

                bos.flush();

            } else {
                System.out.println("请求无效！仅支持HTTP1.0的GET请求。");
            }

            br.close();
            bw.close();
            pw.close();
            socket.close();
            bos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
