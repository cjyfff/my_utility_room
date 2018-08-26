package com.cjyfff.election;

import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.cjyfff.election.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.master.MasterAction;
import com.cjyfff.election.slave.SlaveAction;
import com.cjyfff.repository.ZooKeeperClient;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;

/**
 * Created by jiashen on 2018/8/18.
 */
@Component
public class Election {

    private static final String ELECTION_PATH = "/leader";

    private static final String NODE_INFO_PATH = "/node_info";

    private static final String SHARDING_INFO_PATH = "/sharding_info";

    private static final String ELECTION_STATUS_PATH = "/election_status";

    private static final String CLIENT_ID = UUID.randomUUID().toString();

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private MasterAction masterAction;

    @Autowired
    private SlaveAction slaveAction;

    @PostConstruct
    public void start() {
        try {
            CuratorFramework client = zooKeeperClient.getClient();

            writeNodeInfo(client);

            monitorNodeInfo(client);

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
     * 所有节点，监控集群节点信息（NODE_INFO_PATH）
     * 在选举成功后，发生节点变更，需要触发重新选举
     * @param client
     * @throws Exception
     */
    private void monitorNodeInfo(CuratorFramework client) throws Exception {
        PathChildrenCacheListener cacheListener = (client1, event) -> {
            logger.info("NODE_INFO_PATH listener monitor data change, event type is：" + event.getType());

            if (Lists.newArrayList(Type.CHILD_ADDED, Type.CHILD_REMOVED, Type.CHILD_UPDATED).contains(event.getType())) {

                // 在选举成功后，发生节点变更，需要触发重新选举
                if (ElectionStatusType.FINISH.equals(electionStatus.getElectionFinish())) {
                    electionStatus.setElectionFinish(ElectionStatusType.NOT_YET);
                    logger.info("NODE_INFO_PATH change, start election");

                    electLeader(client);
                }
            }
        };

        PathChildrenCache cache = new PathChildrenCache(client, NODE_INFO_PATH, true);
        cache.getListenable().addListener(cacheListener);
        cache.start();
        // todo: 考虑cache回收问题
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
                try {
                    logger.info("I am Leader");

                    masterAction.masterSetShardingInfo(client);

                    masterAction.masterClaimElectionSuccess(client);

                    masterAction.masterUpdateSelfStatus();

                } catch (Exception e) {
                    logger.error("Master action get error: ", e);
                }

            }

            @Override
            public void notLeader() {
                try {
                    logger.info("I am not Leader");

                    slaveAction.slaveMonitorShardingInfo(client);

                    slaveAction.slaveMonitorElectionStatus(client);

                } catch (Exception e) {
                    logger.error("Slave action get error: ", e);
                }

            }
        });


        leaderLatch.start();
    }
}
