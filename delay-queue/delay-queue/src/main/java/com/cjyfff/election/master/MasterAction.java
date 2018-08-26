package com.cjyfff.election.master;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.cjyfff.election.ElectionStatus;
import com.cjyfff.election.ElectionStatus.ElectionStatusType;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-8-25.
 */
@Component
public class MasterAction {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NODE_INFO_PATH = "/node_info";

    private static final String SHARDING_INFO_PATH = "/sharding_info";

    private static final String ELECTION_STATUS_PATH = "/election_status";

    @Autowired
    private ElectionStatus electionStatus;

    /**
     * master统计节点，分配node id，写入zk
     * @param client
     */
    public void masterSetShardingInfo(CuratorFramework client) throws Exception {
        List<String> nodeIpList = client.getChildren().forPath(NODE_INFO_PATH);
        Integer nodeId = 0;
        Map<Integer, String> shardingMap = Maps.newHashMapWithExpectedSize(15);
        for (String nodeIp : nodeIpList) {
            shardingMap.put(nodeId, nodeIp);
            logger.info("Host: " + nodeIp + "get nodeId: " + nodeId);
            nodeId ++;
        }

        Stat stat = client.checkExists().forPath(SHARDING_INFO_PATH);
        String ShardingInfo = JSON.toJSONString(shardingMap);
        if (stat == null) {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(SHARDING_INFO_PATH, ShardingInfo.getBytes());
        } else {
            client.setData().forPath(SHARDING_INFO_PATH, ShardingInfo.getBytes());
        }

    }

    /**
     * master把选举成功信息写入zk
     * @param client
     * @throws Exception
     */
    public void masterClaimElectionSuccess(CuratorFramework client) throws Exception {
        Stat stat = client.checkExists().forPath(ELECTION_STATUS_PATH);
        if (stat == null) {
            client.create().creatingParentsIfNeeded().forPath(ELECTION_STATUS_PATH,
                ElectionStatusType.FINISH.getValue().toString().getBytes());
        } else {
            client.setData().forPath(ELECTION_STATUS_PATH,
                ElectionStatusType.FINISH.getValue().toString().getBytes());
        }
    }

    /**
     * master设置本机选举状态为完成
     */
    public void masterUpdateSelfStatus() {
        electionStatus.setElectionFinish(ElectionStatusType.FINISH);
        logger.info("*** Election finish. ***");
    }
}
