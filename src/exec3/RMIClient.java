package exec3;

import java.util.Objects;
import java.util.Scanner;

public class RMIClient {

    private String username;
    private String password;


    public static void main(String[] args) {
        RMIClient rmiClient = new RMIClient();
        rmiClient.initial(args);
    }

    private void initial(String[] arguments) {
        if(arguments.length<3) {
            System.out.println("用法：java RMIClient <服务器名> <端口号> {[命令] <命令参数> <命令参数> ...}");
            System.out.println("命令及命令参数用法如下：");
            System.out.println("0.用户登录:    用户登录。");
            System.out.println("       命令: login [用户名] [密码]");
            System.out.println("       示例: login user password");
            help();
        } else {
            switch(arguments[3]) {
                case "register" :
                    if(arguments.length==6)
                        register(arguments[4],arguments[5]);
                    else {
                        System.out.println("【错误】参数错误！");
                        System.out.println("       命令: register [用户名] [密码]");
                        System.out.println("       示例: register user password");
                    }
                    run();
                    break;
                case "add" :
                    if(arguments.length==10) {
                        login(arguments[4],arguments[5]);
                        add(arguments[4],arguments[5],arguments[6],arguments[7],arguments[8],arguments[9]);
                    }
                    else {
                        System.out.println("【错误】参数错误！");
                        System.out.println("       命令: add [用户名] [密码] [与会者用户名] [开始时间] [结束时间] [会议标题]");
                        System.out.println("       示例: add userA password userB 2021-10-19-20:00 2021-10-19-21:00 test");
                    }
                    run();
                    break;
                case "delete":
                    if(arguments.length==7)
                        delete(arguments[4],arguments[5],arguments[6]);
                    else {
                        System.out.println("【错误】参数错误！");
                        System.out.println("       命令: delete [用户名] [密码] [会议号] ");
                        System.out.println("       示例: delete user password 10001");
                    }
                    run();
                    break;

                case "clear":
                    if(arguments.length==6)
                        clear(arguments[4],arguments[5]);
                    else {
                        System.out.println("【错误】参数错误！");
                        System.out.println("       命令: clear [用户名] [密码]");
                        System.out.println("       示例: clear user password");
                    }
                    run();
                    break;

                case "query":
                    if(arguments.length==8)
                        query(arguments[4],arguments[5],arguments[6],arguments[7]);
                    else {
                        System.out.println("【错误】参数错误！");
                        System.out.println("       命令: query [用户名] [密码] [开始时间] [结束时间]");
                        System.out.println("       示例: query username password 2021-10-19-20:00 2021-10-19-21:00");
                    }
                    run();
                    break;

                case "help":
                    run();
                    break;

                case "bye":
                    System.out.println("Bye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("【错误】不支持的命令！");
                    help();
                    run();
            }
        }
    }

    private void run() {
        Scanner sc = new Scanner(System.in);
        help();

    }

    private void help(){
        System.out.println("0.用户注册:    新用户注册。");
        System.out.println("       命令: register [用户名] [密码]");
        System.out.println("       示例: register user password");
        System.out.println("1.添加会议:    添加会议。");
        System.out.println("       命令: add [用户名] [密码] [与会者用户名] [开始时间] [结束时间] [会议标题]");
        System.out.println("       示例: add userA password userB 2021-10-19-20:00 2021-10-19-21:00 test");
        System.out.println("2.删除会议:    根据会议号删除会议。");
        System.out.println("       命令: delete [用户名] [密码] [会议号] ");
        System.out.println("       示例: delete user password 10001");
        System.out.println("3.清除会议：    清除当前用户下的所有会议。");
        System.out.println("       命令: clear [用户名] [密码]");
        System.out.println("       示例: clear user password");
        System.out.println("4.查询会议：    根据时间查询参与的会议。");
        System.out.println("       命令: query [用户名] [密码] [开始时间] [结束时间]");
        System.out.println("       示例: query username password 2021-10-19-20:00 2021-10-19-21:00");
        System.out.println("5.寻求帮助：    获取帮助信息。");
        System.out.println("       命令: help");
        System.out.println("       示例: help");
        System.out.println("6.退出系统：    退出当前系统。");
        System.out.println("       命令: quit");
        System.out.println("       示例: quit");

    }

    private boolean exists(String username) {
        return false;
    }

    private boolean matches(String username, String password) {
        return false;
    }

    private boolean login(String username, String password) {
        if(matches(username,password)){
            this.password=password;
            this.username=username;
            return true;
        } else {
            System.out.println("【错误】：用户名和密码不匹配！");
            return false;
        }
    }

    private boolean register(String username, String password) {
        if(!exists(username)) {
            return login(username,password);
        } else {
            System.out.println("错误：用户名已存在！");
            return false;
        }
    }

    private void add(String username, String password, String participant, String begin, String end, String title) {


    }

    private void delete(String username, String password, String no) {
        if(matches(username,password)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("【警告】：将清除账户<" + username + ">的会议<" + no + ">！请输入[Y]继续。");
            if(Objects.equals(scanner.nextLine(), "Y")) {
                System.out.println("即将执行删除命令。");

            } else {
                System.out.println("删除命令已取消。");
            }
        } else
            System.out.println("【错误】：用户名和密码不匹配！");
    }

    private void clear(String username, String password) {
        if(matches(username,password)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("【警告】：将清除账户<" + username + ">下所有的会议！请输入[Y]继续。");
            if(Objects.equals(scanner.nextLine(), "Y")) {
                System.out.println("即将执行清除命令。");

            } else {
                System.out.println("清除命令已取消。");
            }
        } else
            System.out.println("【错误】：用户名和密码不匹配！");
    }

    private void query(String username, String password, String begin, String end){
        if(matches(username,password)) {

        } else
            System.out.println("【错误】：用户名和密码不匹配！");
    }

}
