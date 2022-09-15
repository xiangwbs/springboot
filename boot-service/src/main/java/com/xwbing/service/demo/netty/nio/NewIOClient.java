package com.xwbing.service.demo.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NewIOClient {
    private static Selector selector;

    public static void main(String[] args) throws IOException {
        selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("localhost", 8080));
        // 需要把socketChannel注册到多路复用器上
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        while (true) {
            // 该方法阻塞，只有当有事件到来时就不会阻塞了
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    handlerConnect(key);
                } else if (key.isReadable()) {
                    handlerRead(key);
                }
            }
        }
    }

    private static void handlerConnect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
        }
        socketChannel.configureBlocking(false);
        socketChannel.write(ByteBuffer.wrap("你好，我是客户端".getBytes()));
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void handlerRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        socketChannel.read(allocate);
        System.out.println("收到服务端数据:" + new String(allocate.array()));
    }
}