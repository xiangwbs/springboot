package com.xwbing.service.demo.zk.zkapi;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZkConnUtil {
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    public static ZooKeeper getZkConn(String zkServer) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(zkServer, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                if (Event.KeeperState.SyncConnected == state) {
                    System.out.println("连接zkServer成功.");
                    COUNT_DOWN_LATCH.countDown();
                }
            }
        });
        COUNT_DOWN_LATCH.await();
        return zookeeper;
    }

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = getZkConn("127.0.0.1:2181");
    }
}






