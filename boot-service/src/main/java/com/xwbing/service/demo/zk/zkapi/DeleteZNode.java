package com.xwbing.service.demo.zk.zkapi;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.ZooKeeper;

public class DeleteZNode {

    private ZooKeeper zooKeeper;

    public DeleteZNode(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    // 同步删除节点
    public void deleteZNodeSync() throws Exception {
        zooKeeper.delete("/zookeeper-api-sync", -1);
    }

    // 异步删除节点
    public void deleteZNodeAsync() {
        zooKeeper.delete("/zookeeper-api-async", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                System.out.println("rc: " + rc);
                System.out.println("path: " + path);
                System.out.println("ctx: " + ctx);
            }
        }, "delete-znode-async");
    }

    public static void main(String[] args) throws Exception {
        DeleteZNode deleteZNode = new DeleteZNode(ZkConnUtil.getZkConn("127.0.0.1:2181"));
        deleteZNode.deleteZNodeSync();
        // deleteZNode.deleteZNodeAsync();
        System.in.read();
    }
}