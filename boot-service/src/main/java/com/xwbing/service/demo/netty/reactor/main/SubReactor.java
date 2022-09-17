package com.xwbing.service.demo.netty.reactor.main;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * 监听并分发read事件
 */
public class SubReactor implements Runnable {
    private Selector selector;

    public SubReactor(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 该方法阻塞，只有当有read事件到来时就不会阻塞了（由于被Acceptor唤醒后，还没有来得及注册read事件，selectionKeys为空，又会循环被阻塞，所以1000ms后再唤起）
                // 监听read事件
                selector.select(1000);
                // 获取所有read事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    dispatch(key);
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        // read事件，Handler
        Runnable runnable = (Runnable)selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
