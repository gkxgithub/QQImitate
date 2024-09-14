package com.atgkx.qqclient.service;

import com.atgkx.qqbean.Message;
import com.atgkx.qqbean.MessageType;

import java.io.*;

public class FileClientService {
    /**
     *
     * @param src 源文件
     * @param dest 把该文件传输到对方的哪个目录
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {
        //首先读取src文件
        Message message = new Message();
        message.setSender(senderId);
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //先将文件进行读取
        FileInputStream fileInputStream =null;
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            //将src中的文件读入到程序的字节数组里
            fileInputStream.read(fileBytes);
            //将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭文件流
            try {
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //提示信息
        System.out.println("\n" + getterId + "给" + getterId + "发送文件：" + src + "到对方的电脑目录" + dest);

        //发送给服务器，由服务器进行转发
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}






















