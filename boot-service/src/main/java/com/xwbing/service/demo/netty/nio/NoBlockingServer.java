package com.xwbing.service.demo.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 1.支持非阻塞
 * 2.数据总是写入buffer,读取也是从buffer中去读
 * 3.可以同时读写
 */
public class NoBlockingServer {
    private static List<SocketChannel> clientChannels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // 得到一个serverSocketChannel管道，这个就等同于ServerSocket，只不过这个是支持异步并且可同时读写
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        while (true) {
            // 然后接收客户端的请求，调用accept,由于设置成非阻塞了，所以accept将不会阻塞在这里等待客户端的连接过来
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 那么不阻塞之后，得到的这个SocketChannel就有可能是null的连接，所以判断是否为空
            if (socketChannel != null) {
                // 同时也设置socketChannel为非阻塞，因为原来我们读取数据read方法也是阻塞的
                socketChannel.configureBlocking(false);
                clientChannels.add(socketChannel);
            } else {
                System.out.println("没有请求过来！！！");
            }
            for (SocketChannel clientChannel : clientChannels) {
                // channel中的数据都是先读取到buffer中，也都先写入到buffer中，所以定义一个ByteBuffer
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                // 数据读取到缓冲区，由于上面设置了非阻塞，此时的read将不会阻塞（一次read就是一次系统调用）
                int num = clientChannel.read(byteBuffer);
                if (num > 0) {
                    System.out.println(
                            "客户端端口：" + clientChannel.socket().getPort() + ",客户端收据：" + new String(byteBuffer.array()));
                } else {
                    System.out.println("等待客户端写数据");
                }
            }
        }
    }
}