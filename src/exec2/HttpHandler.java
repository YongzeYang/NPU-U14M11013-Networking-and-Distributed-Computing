package exec2;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class HttpHandler implements Runnable{
    private Socket socket;
    private String currentPath;
    private String rootPath;
    private String savePath;
    final String CRLF = "\r\n";

    StringBuilder request;
    StringBuilder response;
    StringBuilder html;
   // BufferedReader br;
    BufferedWriter bw;
    PrintWriter pw;
    BufferedReader bufferedReader = null;
    BufferedInputStream inputStream = null;
    BufferedOutputStream outputStream = null;
    String line = "";
    private byte[] buffer;
    String[] header;


    { //初始化代码块。每次创建对象后，都会先于构造器执行。
        request = new StringBuilder();
        response = new StringBuilder();
        html = new StringBuilder();
    }

    /**
     * 构造函数。
     * @param socket 套接字
     * @param path 根目录
     */
    public HttpHandler(Socket socket, String path) {
        this.socket = socket;
        this.currentPath = path;
        this.rootPath = path;
        this.savePath = path + "\\PUT";
    }

    /**
     * 初始化输入与输出流。
     */
    public void initStream() { // 初始化输入输出流对象方法
        try {
         //   br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
           // bw = new BufferedWriter(
             //       new OutputStreamWriter(socket.getOutputStream()));
         //   pw = new PrintWriter(bw, true);
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            System.out.println("新客户端连接。 IP地址: [" + socket.getInetAddress() + ":"
                    + socket.getPort() + "]"); //客户端信息
            initStream();
            System.out.println();
            processRequest();
            header = line.split(" ");
            if(header.length==3) {
                if(header[2].equals("HTTP/1.0") || header[2].equals("HTTP/1.1")){
                    System.out.println(Arrays.toString(header));
                    if(header[0].equals("GET")) {
                        System.out.println("get");
                        doGet();
                    } else if(header[0].equals("PUT")) {
                        doPut();
                    } else {
                        System.out.println("仅支持GET和PUT请求。");
                        doBadRequest();
                    }
                } else {
                    System.out.println("仅支持HTTP/1.0或HTTP/1.1请求。");
                    doBadRequest();
                }
            } else {
                System.out.println("无效请求。");
                doBadRequest();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void processRequest() {
        try {
            int last=0,c=0;
            boolean inHeader = true;
            boolean flag = false;//用flag变量来判断第一行是否读完，利用第一行来处理http报文
            String mark = "";
            while(inHeader && (c=inputStream.read())!=-1){
                switch (c){
                    case '\r':
                        break;
                    case  '\n':
                        if(c==last){
                            inHeader = false;
                            break;
                        }
                        last = c;
                        request.append(mark+"\n");
                        if(!flag){//如果第一行读完了，把第一行赋值给line，line负责后续处理
                            line = mark;
                        }
                        mark = "";
                        flag = true;
                        break;
                    default:
                        last = c;
                        mark += (char) c;
                }
            }
            System.out.println("客户端"+socket.getInetAddress()+socket.getPort()+"发送HTTP请求，报文如下：");
            System.out.println(request);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 处理Get请求。
     * 客户端请求指定的页面信息，并返回实体主体。
     */
    private void doGet() {
    if(header[1].equals("/")||header[1].equals("/index.html")) {
        File file = new File(rootPath+"\\index.html");
        sendHtml(file);
    } else if (header[1].endsWith(".html")||header[1].endsWith(".htm")) {
        File file = new File(rootPath+"\\" +header[1]);
        if(file.exists()) sendHtml(file);
        else doNotFound();
    } else if (header[1].endsWith(".jpg")) {
        File file = new File(rootPath+"\\" +header[1]);
        if(file.exists()) sendJpg(file);
        else doNotFound();
    }


    }

    /**
     * 处理Put请求。
     * 客户端向服务器端传送数据取代指定文档的内容。
     */
    private void doPut() {
        try {
            String responseHeader =new String();
            responseHeader = "HTTP/1.1 200 OK" + CRLF;
            responseHeader += "Server:MyHttpServer/1.1 " + CRLF;
            //responseHeader += "Content-length: " + file.length() + CRLF;
            responseHeader += "Content-type: " + "text/html;charset=ISO-8859-1" + CRLF + CRLF;
            //往客户端发送
            String message1 = responseHeader;
            System.out.println("返回的响应头：\n"+message1);
            buffer = message1.getBytes();
            outputStream.write(buffer, 0, message1.length());
            outputStream.flush();


            String[] part = line.split(" ");
            savePath = savePath+part[1].replaceAll("/","\\\\");
            buffer = new byte[8196];

            int c=0;
            File file = new File(savePath);

            FileOutputStream fos = new FileOutputStream(file);
            //bufferedinputstream只有关闭socket才会不阻塞，所以需要用available函数查看是否读完
            while ((inputStream.available()>0)&&((c = inputStream.read())!= -1)) {

                fos.write(c);
                fos.flush();
            }
            fos.flush();
            fos.close();

            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 服务器收到一个无效的响应。
     * 返回502 Bad Gateway。
     */
    private void doBadRequest() {
        try {
            File file = new File (rootPath + "\\response\\400.html");
            bufferedReader = new BufferedReader(new FileReader(file.getPath()));
            String str = bufferedReader.readLine();
            while (str != null) {
                html.append(str).append("\n");
                str = bufferedReader.readLine();
            }
            response.append("HTTP/1.1 400 Bad Request" + CRLF);
            response.append("Date: ").append(new Date().toString()).append(CRLF);
            response.append("Content-Type: text/html;charset=ISO-8859-1" + CRLF);
            response.append("Content-Length: ").append(file.length()+106).append(CRLF);
            response.append(CRLF);
            response.append(html);
            //往客户端发送
            String message = response.toString();
            System.out.println("返回的响应信息如下：\n"+message);
            buffer = message.getBytes();
            outputStream.write(buffer,0,message.length());
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器无法找到被请求的资源对象。
     * 返回404 Not Found。
     */
    private void doNotFound() {
        try {
            File file = new File (rootPath + "\\response\\404.html");
            bufferedReader = new BufferedReader(new FileReader(file.getPath()));
            String str = bufferedReader.readLine();
            while (str != null) {
                html.append(str).append("\n");
                str = bufferedReader.readLine();
            }
            response.append("HTTP/1.1 404 Not Found" + CRLF);
            response.append("Date: ").append(new Date().toString()).append(CRLF);
            response.append("Content-Type: text/html;charset=ISO-8859-1" + CRLF);
            response.append("Content-Length: ").append(file.length()+106).append(CRLF);
            response.append(CRLF);
            response.append(html);
            //往客户端发送
            String message = response.toString();
            System.out.println("返回的响应信息如下：\n"+message);
            buffer = message.getBytes();
            outputStream.write(buffer,0,message.length());
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendHtml(File file) {
        try {
            if(file.exists()){
                bufferedReader = new BufferedReader(new FileReader(file.getPath()));
                String str = bufferedReader.readLine();
                while (str != null) {
                    html.append(str).append("\n");
                    str = bufferedReader.readLine();
                }
                //包装response

                response.append("HTTP/1.1 200 OK" + CRLF);
                response.append("Date: ").append(new Date().toString()).append(CRLF);
                response.append("Content-Type: text/html;charset=ISO-8859-1" + CRLF);
                response.append("Content-Length: ").append(file.length()).append(CRLF);
                response.append(CRLF);
                response.append(html);
                //往客户端发送
                String message = response.toString();
                System.out.println("返回的响应信息如下：\n"+message);
                buffer = message.getBytes();
                outputStream.write(buffer,0,message.length());
                outputStream.flush();
                outputStream.close();
                socket.close();
            } else {
                System.out.println("文件不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendJpg(File file) {
        try{
            //包装response
            response.append("HTTP/1.1 200 OK" + CRLF);
            response.append("Date: " + new Date().toString() + CRLF);
            response.append("Content-Type: image/jpeg;charset=ISO-8859-1" + CRLF);
            response.append("Content-Length: " + file.length() + CRLF);
            response.append(CRLF);
            //往客户端发送
            String message = response.toString();
            buffer = message.getBytes("ISO-8859-1");
            outputStream.write(buffer, 0, message.length());
            //将文件读取至内存中并发送
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            int length = 0;
            byte[] sendInfo = new byte[8192];
            while((length = bis.read(sendInfo))!=-1){
                System.out.println(length);
                outputStream.write(sendInfo,0,length);
                outputStream.flush();
            }
            outputStream.close();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
