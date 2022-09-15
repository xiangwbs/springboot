package com.xwbing.service.demo.netty.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class BlockingClient {
    public static void main(String[] args) throws IOException {
        // 创建一个socket连接，访问localhost，8080端口的服务端
        Socket socket = new Socket("localhost", 8080);
        // 获取socket的输出流，把数据写入到服务端
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // 客户端向服务端数据写入，一定要有数据结束符，不然服务端不知道自己读取完成没有
        bufferedWriter.write("我是客户端，收到请回答！！\n");
        bufferedWriter.flush();
        // 获取socket的输入流，此处是获取服务端给客户端回写的数据
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // 读取数据
        String serStr = bufferedReader.readLine();
        System.out.println(serStr);
    }
}