package com.xwbing.service.demo.netty.reactor.main;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 处理accept事件并初始化多个SubReactor
 */
public class Acceptor implements Runnable {
    private ServerSocketChannel serverSocketChannel;
    private int index = 0;
    private int core = Runtime.getRuntime().availableProcessors() * 2;
    private Selector[] selectors = new Selector[core];
    private SubReactor[] subReactors = new SubReactor[core];
    private Thread[] threads = new Thread[core];

    public Acceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        // 初始化多个SubReactor
        this.serverSocketChannel = serverSocketChannel;
        for (int i = 0; i < core; i++) {
            selectors[i] = Selector.open();
            subReactors[i] = new SubReactor(selectors[i]);
            threads[i] = new Thread(subReactors[i]);
            // 启动每一个subReactor线程
            threads[i].start();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("线程名称：" + Thread.currentThread().getName());
            // 得到一个客户端连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 唤醒阻塞的selector
            selectors[index].wakeup();
            // 然后注册read事件到该selector
            socketChannel.register(selectors[index], SelectionKey.OP_READ, new Handler(socketChannel));
            // index++
            if (++index == threads.length) {
                index = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}