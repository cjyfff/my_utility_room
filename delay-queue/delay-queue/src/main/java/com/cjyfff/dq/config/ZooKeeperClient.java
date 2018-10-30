package com.cjyfff.dq.config;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/8/18.
 */
@Component
public class ZooKeeperClient {

    @Value("${delay_queue.zk_host}")
    private String zkHost;

    @Value("${delay_queue.zk_session_timeout_ms}")
    private Integer zkSessionTimeoutMs;

    @Value("${delay_queue.zk_connection_timeout_ms}")
    private Integer zkConnectionTimeoutMs;

    private CuratorFramework client;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CuratorFramework getClient() {
        if (this.client == null) {
            // todo: 连接不上zk应该直接退出
            CuratorFramework c = CuratorFrameworkFactory.newClient(zkHost, zkSessionTimeoutMs, zkConnectionTimeoutMs,
                new ExponentialBackoffRetry(1000, 3));
            c.start();
            this.client = c;
        }
        return this.client;
    }

    @PreDestroy
    public void closeClient() {
        if (! CuratorFrameworkState.STOPPED.equals(this.client.getState())) {
            this.client.close();
        }
        logger.info("Close zookeeper connection.");
    }
}
