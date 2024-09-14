package com.atgkx.qqclient.view;

import com.atgkx.qqclient.service.FileClientService;
import com.atgkx.qqclient.service.MessageClientService;
import com.atgkx.qqclient.service.UserClientService;
import com.atgkx.qqclient.util.Utility;

import java.io.IOException;

/**
 * 客户端的菜单页面
 */
public class QQView {
    private boolean loop = true;//控制是否显示菜单
    private String key = "";//接收用户的键盘输入

    private UserClientService userClientService = new UserClientService();//对象是用于登录服务器和注册用户

    MessageClientService messageClientService = new MessageClientService();//用于用户之间的通信

    private FileClientService fileClientService = new FileClientService();//该对象用于传输文件


    public static void main(String[] args) throws IOException, ClassNotFoundException{
        new QQView().mainMenu();
        System.out.println("客户端退出系统......");
    }




    //显示主菜单
    private void mainMenu() throws IOException, ClassNotFoundException{
        while (loop){
            System.out.println("==========欢迎登录网络通讯系统==========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 2 注册用户");
            System.out.println("\t\t 9 退出系统");

            key = Utility.readString(1);
            //根据用户的输入，进行不同的逻辑
            switch (key) {
                case "1":
                    System.out.println("请输入用户号：");
                    String userId = Utility.readString(50);
                    System.out.println("请输入密 码：");
                    String pwd = Utility.readString(50);
                    //需要到服务端去验证该用户是否合法
                    if(userClientService.checkUser(userId, pwd)){
                        System.out.println("==========欢迎（用户" + userId + "）==========");
                        //进入到二级菜单
                        while (loop){
                            System.out.println("==========网络通信系统二级菜单（用户" + userId + "登录成功)==========");
                            System.out.println("\t\t 1 显示在线用户列表");
                            System.out.println("\t\t 2 群发消息");
                            System.out.println("\t\t 3 私聊消息");
                            System.out.println("\t\t 4 发送文件");
                            System.out.println("\t\t 5 离线留言");
                            System.out.println("\t\t 6 发表个性签名");
                            System.out.println("\t\t 7 查看好友个性签名");
                            System.out.println("\t\t 9 退出系统");
                            System.out.println("请输入你的选择：");
                            key = Utility.readString(1);
                            switch (key) {
                                case "1":
                                    //                                    System.out.println("显示在线用户列表");
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.println("请输入你想对大家说的话");
                                    String content2 = Utility.readString(100);
                                    messageClientService.sendMessageToAll(content2, userId);
//                                    System.out.println("群发消息");
                                    break;
                                case "3":
                                    System.out.println("请输入想聊天的用户号（在线）：");
                                    String getter = Utility.readString(50);
                                    System.out.println("请输入你想说的话：");
                                    String content = Utility.readString(100);
                                    messageClientService.sendMessageToOne(content, userId, getter);
//                                    System.out.println("私聊消息");
                                    break;
                                case "4":
                                    System.out.print("请输入你想把文件发送给的用户");
                                    getter = Utility.readString(50);
                                    System.out.print("请输入你希望发送的文件的路径(例如:d:\\xx.jpg)");
                                    String src = Utility.readString(100);
                                    System.out.print("请输入你希望保存到对方的文件的路径(例如:d:\\xx.jpg)");
                                    String dest = Utility.readString(100);
                                    fileClientService.sendFileToOne(src,dest,userId,getter);
                                    break;
                                case "5":
//                                    System.out.println("离线留言");
                                    System.out.println("你想对谁留言：");
                                    getter = Utility.readString(50);
                                    System.out.println("请输入留言内容：");
                                    content = Utility.readString(100);
                                    messageClientService.sendMessageToOutLine(content,userId,getter);
                                    break;
                                case "6":
//                                    System.out.println("发表个性签名");
                                    System.out.println("请输入想发表的个性签名：");
                                    String content3 = Utility.readString(500);
                                    userClientService.writePersonSign(content3);
                                    break;
                                case "7":
//                                    System.out.println("查看个性签名");
                                    System.out.println("请输入想要查看的用户名");
                                    userId = Utility.readString(50);
                                    userClientService.getPersonSign(userId);
                                    //确保主线程在信息输出之后再继续执行
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                case "9":
                                    //调用一个方法，给服务器发送退出系统的message
                                    userClientService.logout();
                            }
                        }
                    } else {
                        System.out.println("==========登录失败==========");
                    }
                    break;
                case "2":
                    System.out.println("请输入注册用户号：");
                    String userId1 = Utility.readString(50);
                    System.out.println("请输入注册密 码：");
                    String pwd1 = Utility.readString(50);
                    //需要到服务端去验证该用户是否合法
                    if(userClientService.signUser(userId1,pwd1))
                        System.out.println("您已注册成功，请登录");
                    else
                        System.out.println("用户名已被占用");
                    break;
                case "9":
                    loop = false;
                    break;
            }
        }
    }
}
