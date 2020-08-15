package com.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author cjyfff
 * @date 2020/8/8 11:25
 */
public class ZkTest {

    private CuratorFramework client;

    @Before
    public void initMethod() {
        CuratorFramework c = CuratorFrameworkFactory.newClient("192.168.3.103:2181", 30000,
                10000, new ExponentialBackoffRetry(1000, 3));
        c.start();
        this.client = c;
    }

    /**
     * 使用版本号实现自增id
     */
    @Test
    public void testVersion() {
        final String path = "/test";

        try {
            for (int j = 0; j < 1; j++) {

                Thread t1 = new Thread(() -> {
                    for (int i = 0; i < 100; i++) {
                        try {
                            System.out.println(next(path));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                t1.start();
                t1.join();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int next(String path) throws Exception {
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path, new byte[0]);
            stat = client.checkExists().forPath(path);
        }

        // 下面分了两步走，不是原子操作，会有问题，
        // 原生包使用Stat stat = zk.setData(path, new byte[0], -1)就没有此问题
        client.setData().forPath(path, new byte[0]);
        stat = client.checkExists().forPath(path);
        return stat.getVersion();

    }

    @After
    public void afterMethod() {
        client.close();
    }
}
