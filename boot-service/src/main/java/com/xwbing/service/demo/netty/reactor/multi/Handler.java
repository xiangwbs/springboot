package com.xwbing.service.demo.netty.reactor.multi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 处理read事件
 */
public class Handler implements Runnable {
    private SocketChannel socketChannel;
    private Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public Handler(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        System.out.println("线程名称：" + Thread.currentThread().getName());
        executor.execute(new ReaderHandler(socketChannel));
    }

    static class ReaderHandler implements Runnable {
        SocketChannel socketChannel;

        public ReaderHandler(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            System.out.println("线程名称：" + Thread.currentThread().getName());
            //  定义一个ByteBuffer的数据结构
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int length;
            // message为客户端传送过来的数据
            String message = "";
            try {
                do {
                    length = socketChannel.read(byteBuffer);
                    if (length > 0) {
                        message += new String(byteBuffer.array());
                    }
                } while (length > byteBuffer.capacity());
                System.out.println(socketChannel.getRemoteAddress() + ": Server receive Msg:" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
