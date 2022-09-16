package com.xwbing.service.demo.zk.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorApi {
    public static void main(String[] args) {
        String connectStr = "127.0.0.1:2181";
        //@formatter:off
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .builder()
                .connectionTimeoutMs(20000)
                .connectString(connectStr)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        //@formatter:on
        curatorFramework.start();
        try {
            // 创建节点
            String znode = curatorFramework.create().withMode(CreateMode.PERSISTENT)
                    .forPath("/curator-api", "666".getBytes());
            System.out.println("创建节点成功: " + znode);

            // 查询节点
            byte[] bytes = curatorFramework.getData().forPath(znode);
            System.out.println("节点curator-api 数据查询成功: " + new String(bytes));

            // 修改节点
            curatorFramework.setData().forPath(znode, "888".getBytes());
            System.out.println("节点curator-api 数据修改成功.");

            // 删除节点
            curatorFramework.delete().forPath(znode);
            System.out.println("节点curator-api 已被删除.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}