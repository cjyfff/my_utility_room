package com.cjyfff.repository;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/8/18.
 */
@Component
public class ZooKeeperClient {

    private static final String ZK_HOST = "192.168.43.48:2181";

    private CuratorFramework client;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CuratorFramework getClient() {
        if (this.client == null) {
            // todo: 连接不上zk应该直接退出
            CuratorFramework c = CuratorFrameworkFactory.newClient(ZK_HOST,
                new ExponentialBackoffRetry(1000, 3));
            c.start();
            this.client = c;
        }
        return this.client;
    }

    @PreDestroy
    public void closeClient() {
        this.client.close();
        logger.info("Close zookeeper connection.");
    }
}
