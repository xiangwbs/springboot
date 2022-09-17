package com.xwbing.service.demo.netty.reactor.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 主从Reactor多线程模型
 * 监听和分发accept事件
 */
public class MainReactor implements Runnable {
    private final Selector selector;

    public MainReactor(int port) throws IOException {
        // 得到一个多路复用器
        selector = Selector.open();
        // 获取一个管道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        // 把连接事件注册到多路复用器上，并通过attachment传递Acceptor对象去处理accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(serverSocketChannel));
    }

    @Override
    public void run() {
        // 只要没有中断，就一直等待客户端过来
        while (!Thread.interrupted()) {
            try {
                // 该方法阻塞，只有当有事件到来时就不会阻塞了
                // 1.监听accept事件
                selector.select();
                // 获取所有accept事件，事件都被封装成SelectionKey
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterable = selectionKeys.iterator();
                while (iterable.hasNext()) {
                    // 2.分发accpet事件
                    dispatch(iterable.next());
                    iterable.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        // accept事件，Acceptor
        Runnable runnable = (Runnable)selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}