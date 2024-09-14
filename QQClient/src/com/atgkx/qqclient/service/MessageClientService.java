package com.atgkx.qqclient.service;

import com.atgkx.qqbean.Message;
import com.atgkx.qqbean.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;


/**
 * 提供和消息相关的服务方法
 */
public class MessageClientService {
    /**
     * 给一个用户发送消息
     * @param content
     * @param senderId
     * @param getterId
     */
    public void sendMessageToOne(String content, String senderId,String getterId) {
        //创建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString());

        System.out.println(senderId + "对" + getterId + "说" +content);

        //发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 给所有在线用户发送消息
     * @param content
     * @param senderId
     */
    public void sendMessageToAll(String content, String senderId) {
        //创建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_TOALL_MES);
        message.setSender(senderId);
        message.setContent(content);
        message.setSendTime(new Date().toString());

        System.out.println(senderId + "对大家说" + content);
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 给离线用户留言
     * @param content
     * @param senderId
     * @param getterId
     */
    public void sendMessageToOutLine(String content, String senderId, String getterId) {
        //创建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_MSG);
        message.setSender(senderId);
        message.setContent(content);
        message.setGetter(getterId);
        message.setSendTime(new Date().toString());
        System.out.println("已经向" + getterId + "留言");

        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
