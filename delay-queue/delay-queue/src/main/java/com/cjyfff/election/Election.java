package com.cjyfff.election;

import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.cjyfff.repository.ZooKeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/8/18.
 */
@Component
public class Election {

    private static final String ELECTION_PATH = "/leader";

    private static final String NODE_INFO_PATH = "/node_info";

    private static final String CLIENT_ID = UUID.randomUUID().toString();

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @PostConstruct
    public void start() {
        try {
            CuratorFramework client = zooKeeperClient.getClient();

            writeNodeInfo(client);

            electLeader(client);
        } catch (Exception e) {
            logger.error("Starting election get error: ", e);
            System.exit(1);
        }

    }

    private void writeNodeInfo(CuratorFramework client) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress();

        client.create().withMode(CreateMode.EPHEMERAL).forPath(NODE_INFO_PATH, ip.getBytes());
    }

    public void electLeader(CuratorFramework client) throws Exception {
        LeaderLatch leaderLatch = new LeaderLatch(client, ELECTION_PATH, "Client #" + CLIENT_ID);

        leaderLatch.addListener(new LeaderLatchListener() {

            @Override
            public void isLeader() {
                logger.info("I am Leader");
            }

            @Override
            public void notLeader() {
                logger.info("I am not Leader");
            }
        });


        leaderLatch.start();
    }
}
