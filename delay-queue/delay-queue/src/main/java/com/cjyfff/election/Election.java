package com.cjyfff.election;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSON;

import com.cjyfff.repository.ZooKeeperClient;
import com.google.common.collect.Maps;
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

    private static final String SHARDING_INFO_PATH = "/sharding_info_path";

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

    /**
     * 把自身ip作为临时节点写入zk
     * @param client
     * @throws Exception
     */
    private void writeNodeInfo(CuratorFramework client) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress();
        String myNodeInfoPath = NODE_INFO_PATH + "/" + ip;
        client.create().withMode(CreateMode.EPHEMERAL).forPath(myNodeInfoPath, "".getBytes());
    }

    /**
     * master统计节点，分配node id，写入zk
     * @param client
     */
    private void masterSetShardingInfo(CuratorFramework client) throws Exception {
        List<String> nodeIpList = client.getChildren().forPath(NODE_INFO_PATH);
        Integer nodeId = 0;
        Map<Integer, String> shardingMap = Maps.newHashMapWithExpectedSize(15);
        for (String nodeIp : nodeIpList) {
            shardingMap.put(nodeId, nodeIp);
            logger.info("Host: " + nodeIp + "get nodeId: " + nodeId);
            nodeId ++;
        }

        client.create().withMode(CreateMode.EPHEMERAL)
            .forPath(SHARDING_INFO_PATH,
                JSON.toJSONString(shardingMap).getBytes());

    }

    /**
     * 选举master
     * @param client
     * @throws Exception
     */
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
