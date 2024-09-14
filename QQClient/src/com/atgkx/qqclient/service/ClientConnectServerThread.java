package com.atgkx.qqclient.service;

import com.atgkx.qqbean.Message;
import com.atgkx.qqbean.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 持有socket的线程
 */
public class ClientConnectServerThread extends Thread{
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接收一个Socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    //为了更方便的得到Socket
    public Socket getSocket(){
        return socket;
    }

    @Override
    public void run(){
        //显示一次就行了
        System.out.println("客户端线程，等待从读取从服务器端发送的消息");
        //Thread需要在后台和服务器通信，因此while循环
        while(true){
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送Message对象，线程将会阻塞在这里
                //从而时刻保持接收客户端传递过来的Message对象
                Message message = (Message) ois.readObject();

                //判断这个message类型，然后做相应的业务处理


                //判断是否是返回在线列表
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线列表信息，并显示
                    //规定
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n=======当前在线用户列表=======");
                    for(int i = 0; i< onlineUsers.length; i++) {
                        System.out.println("用户：" + onlineUsers[i]);
                    }
                }

                //判断是否是返回私聊信息
                else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {//普通的聊天消息
                    //将转发的消息显示到控制台
                    System.out.println("\n" + message.getSender() + "对" + "你" + "说：" + message.getContent());
                }

                //判断是否是返回群聊信息
                else if (message.getMesType().equals(MessageType.MESSAGE_TOALL_MES)) {
                    //显示在客户端的控制台
                    System.out.println("\n" + message.getSender() + "对大家说：" + message.getContent());
                }

                //判断是否是返回文件信息
                else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println(
                            "\n" + message.getSender() + "给" + message.getGetter() + "发送了文件：" + message.getSrc() +
                                    "，保存在：" + message.getDest());
                    //取出message的文件字节数组，通过文件输出流写入到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n保存文件成功~");
                }

                //判断是否是返回签名信息
                else if(message.getMesType().equals(MessageType.MESSAGE_RET_PERSONSIGN)) {
                    ArrayList<String> personSignList = message.getPersonSignList();
                    for(int i = 0; i < personSignList.size(); i++) {
                        System.out.println(message.getGetter() + ":" + personSignList.get(i));
                    }
                }
//                else if (message.getMesType().equals(MessageType.MESSAGE_MSG)) {
//                    String[] outLineMsg = message.getContent().split(" ");
//                    for(int i = 0; i< outLineMsg.length; i++) {
//                        System.out.println(message.getSendTime() + "   " + message.getSender() + "给你发来留言:" + outLineMsg[i]);
//                    }
//                }
                else {
                    System.out.println("其它类型的message，现在暂时不处理......");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
