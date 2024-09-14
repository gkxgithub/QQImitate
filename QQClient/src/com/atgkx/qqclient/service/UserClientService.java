package com.atgkx.qqclient.service;

import com.atgkx.qqbean.Message;
import com.atgkx.qqbean.MessageType;
import com.atgkx.qqbean.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 用户登录验证和注册以及留言和获取在线用户
 */
public class UserClientService {

    private User u = new User();//因为我们可能在其它地方使用到user信息，因此做成一个成员属性

    private Socket socket;//因为Socket在其它地方也可能使用，因此做成属性

    /**
     * 根据userId和pwd检测用户名和密码是否合法
     * @param userId
     * @param pwd
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public boolean checkUser(String userId, String pwd) throws IOException, ClassNotFoundException{
        boolean b = false;


        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        u.setIfSign(false);
        socket = new Socket(InetAddress.getLocalHost(), 9999);

        //得到ObjectOutputStream对象
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(u);//发送User对象

        //读取从服务器回复的Message对象
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) ois.readObject();

        if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录成功

            //创建一个和服务器保持通信的线程-> 创建一个类 ClientConnectServerThread
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            //启动客户端的线程
            clientConnectServerThread.start();
            //为了后面客户端的扩展，我们将线程放入到集合中管理
            ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

            b= true;
        } else if(ms.getMesType().equals(MessageType.MESSAGE_MSG)) {//登录成功并且有留言

            //创建一个和服务器保持通信的线程-> 创建一个类 ClientConnectServerThread
            ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
            //启动客户端的线程
            clientConnectServerThread.start();
            //为了后面客户端的扩展，我们将线程放入到集合中管理
            ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);

            b= true;

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String[] outLineMsg = ms.getContent().split(" ");
            for(int i = 0; i< outLineMsg.length; i++) {
                System.out.println(outLineMsg[i]);
            }
        } else {
            //如果登录失败，则不能启动和服务器通信的线程，关闭socket
            socket.close();
        }

        return b;
    }

    /**
     * 注册新用户
     * @param userId
     * @param pwd
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public boolean signUser(String userId, String pwd) throws IOException, ClassNotFoundException{
        boolean b = false;

        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        u.setIfSign(true);
        socket = new Socket(InetAddress.getLocalHost(), 9999);

        //得到ObjectOutputStream对象
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(u);//发送User对象

        //读取从服务器回复的Message对象
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message ms = (Message) ois.readObject();

        if(ms.getMesType().equals(MessageType.MESSAGE_SIGN_SUCCEED)) {//注册成功
            b= true;
        }

        return b;
    }

    /**
     * 向服务器端请求在线用户列表
     */
    public void onlineFriendList() {
        //发送一个Message对象，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());

        //发送给服务器
        try {
            //得到当前线程的Socket对应的ObjectOutputStream对象
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);//发送一个Message对象，向服务端要求在线用户列表
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出客户端，并给服务端发送一个退出系统的message对象
     */
    public void logout() {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//需要指定为哪一个客户端id

        //发送message
        try {
            //此处，建议利用这种长的写法，而不是直接调用上面的socket，因为你想啊，现在每开一个客户端为一个进程，如果现在一个进程有不止一个线程的话，就
            //并不清楚获取哪一个了。但是目前这种的如果只是一个进程开了一个线程倒是无所谓。
            //利用一个System.exit(0)就表示直接退出了这个进程。
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println(u.getUserId() + "退出系统");
//            socket.close();
            System.exit(0);//结束进程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将签名写入服务器
     * @param conent
     */
    public void writePersonSign(String conent){
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_SET_PERSONSIGN);
        message.setSender(u.getUserId());
        message.setContent(conent);
        //发送message
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定用户的个性签名
     * @param getterId
     */
    public void getPersonSign(String getterId) {
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_PERSONSIGN);
        //想要获取的签名人的id
        message.setSender(u.getUserId());
        message.setGetter(getterId);

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}






















