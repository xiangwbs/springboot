package com.xwbing.demo.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室客户端
 * tcp通讯的客户端
 *
 * @author xiangwb
 */
public class Client {
    /*
     * socket 套接字 封装了tcp协议
     */
    private Socket socket;

    /*
     * 构造方法，用于初始化客户端，若初始化过程出现错误会抛出异常
     */
    public Client() throws Exception {
        try {
            /*
             * 初始化socket需要传入两个参数： 1：远程计算机的ip地址 2：服务端应用程序在服务器上申请的端口
             * 我们是通过ip地址找到服务器的计算机，在通过端口找到该机器上的服务端应用 程序，这个端口不是客户端决定的，而是服务端决定的
             *
             * 实例化socket的过程就是连接的过程 若服务端没有响应，这里会抛出异常
             */
            socket = new Socket("localhost", 7851);
        } catch (Exception e) {
            throw e;
        }
    }

    public void start() {
        try {
            // 启动用于接收服务端发送消息的线程
            ServerHandler handler = new ServerHandler();
            Thread t = new Thread(handler);
            t.start();
            /*
             * outputstream getoutputstream（） 通过socket获取输出流，用来将数据发送至服务端
             */
            Scanner sc = new Scanner(System.in);
            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out, "utf-8");
            PrintWriter pw = new PrintWriter(osw, true);

            while (true) {
                String say = sc.nextLine();
                pw.println(say);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该线程任务是用来接收服务端发送过来的消息并输出到客户端的控制台上
     */
    private class ServerHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "utf-8"));
                String message;
                while ((message = br.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
