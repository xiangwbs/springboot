package com.xwbing.service.demo.netty.proactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class Proactor implements Runnable {
    public CountDownLatch latch;
    public AsynchronousServerSocketChannel serverSocketChannel;

    public Proactor(int port) throws IOException {
        serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        System.out.println("服务器已启动，端口号：" + port);
    }

    @Override
    public void run() {
        // 因为是异步的（aio），所以要阻塞线程让程序不结束
        latch = new CountDownLatch(1);
        // 注册Accept事件由AcceptorHandler处理
        serverSocketChannel.accept(this, new AcceptorHandler());
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}