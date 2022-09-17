package com.xwbing.service.demo.netty.reactor.multi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 处理accept事件，分发read事件
 */
public class Acceptor implements Runnable {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public Acceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        try {
            System.out.println("线程名称：" + Thread.currentThread().getName());
            // 得到一个客户端连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println(socketChannel.getRemoteAddress() + ":收到一个客户端连接");
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 注册read事件
            socketChannel.register(selector, SelectionKey.OP_READ, new Handler(socketChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}