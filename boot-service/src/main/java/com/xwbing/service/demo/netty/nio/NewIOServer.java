package com.xwbing.service.demo.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NewIOServer {
    private static Selector selector;

    public static void main(String[] args) throws IOException {
        // 得到一个多路复用器
        selector = Selector.open();
        // 获取一个管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        // 把连接事件注册到多路复用器上,通过注册不同事件处理不同的任务，
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 该方法阻塞，只有当有事件到来时就不会阻塞了
            selector.select();
            // 获取所有的事件，事件都被封装成SelectionKey
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                // 获取到相应的事件key
                SelectionKey key = iterator.next();
                // 拿到后要删除，防止再次调用
                iterator.remove();
                if (key.isAcceptable()) {
                    // 连接事件
                    handlerAccept(key);
                } else if (key.isReadable()) {
                    // 读的就绪事件
                    handlerRead(key);
                }
            }
        }
    }

    private static void handlerAccept(SelectionKey key) throws IOException {
        // 从selector中获取ServerSocketChannel，因为当初把ServerSocketChannel注册再selector上，并且注册的accept事件
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        // 能到这里，一定时有客户端连接过来，所以一定会有连接
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        // 给客户端回写数据
        socketChannel.write(ByteBuffer.wrap("连接成功\\n".getBytes()));
        // 然后注册read事件，等while的循环再次获取read事件，然后读取SocketChannel中的数据
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private static void handlerRead(SelectionKey key) throws IOException {
        // 获取SocketChannel
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        // 读取客户端的数据，其实这个里面不一定有值
        socketChannel.read(allocate);
        System.out.println("收到客户端数据:" + new String(allocate.array()));
    }
}