package com.cjyfff.election;

import java.net.InetAddress;
import java.util.UUID;

import javax.annotation.PostConstruct;

import com.cjyfff.election.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.master.MasterAction;
import com.cjyfff.election.slave.SlaveAction;
import com.cjyfff.repository.ZooKeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private MasterAction masterAction;

    @Autowired
    private SlaveAction slaveAction;

    @Value("${server.port}")
    private String servicePort;

    @PostConstruct
    public void start() {
        try {
            CuratorFramework client = zooKeeperClient.getClient();

            writeNodeInfo(client);

            electLeader(client);

            slaveAction.slaveMonitorShardingInfo(client);

            slaveAction.slaveMonitorElectionStatus(client);

        } catch (Exception e) {
            logger.error("Starting election get error: ", e);
            System.exit(1);
        }

    }

    /**
     * 把自身ip和端口作为临时节点路径写入zk
     * @param client
     * @throws Exception
     */
    private void writeNodeInfo(CuratorFramework client) throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        String host = addr.getHostAddress() + ":" + servicePort;
        String myNodeInfoPath = NODE_INFO_PATH + "/" + host;
        Stat stat = client.checkExists().forPath(myNodeInfoPath);
        if (stat == null) {
            client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(myNodeInfoPath, "".getBytes());
        }
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

                    if (ElectionStatusType.FINISH.equals(electionStatus.getElectionFinish())) {
                        logger.info("Starting re-election...");
                        masterAction.masterClaimElectionStatus(client, false);

                        masterAction.masterUpdateSelfStatus(false);
                    }

                    masterAction.masterSetShardingInfo(client);

                    masterAction.masterMonitorNodeInfo(client);

                    masterAction.masterClaimElectionStatus(client, true);

                    // todo: 处理本机设置选举成功后，node info change listener才回调导致2次分片的问题
                    masterAction.masterUpdateSelfStatus(true);

                } catch (Exception e) {
                    logger.error("Master action get error: ", e);
                }

            }

            @Override
            public void notLeader() {
                // 本服务没有主动放弃master的逻辑，notLeader应该永远不被触发
                logger.error("`notLeader` method should never not be call...");
            }
        });


        leaderLatch.start();
        electionStatus.setLeaderLatch(leaderLatch);
    }
}