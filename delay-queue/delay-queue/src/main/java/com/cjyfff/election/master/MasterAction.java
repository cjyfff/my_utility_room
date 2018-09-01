package com.cjyfff.election.master;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.cjyfff.election.ElectionListener;
import com.cjyfff.election.ElectionStatus;
import com.cjyfff.election.ElectionStatus.ElectionStatusType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
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

    @Autowired
    private ElectionListener electionListener;

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
            logger.info("Host: " + nodeIp + ", get nodeId: " + nodeId);
            nodeId ++;
        }

        Stat stat = client.checkExists().forPath(SHARDING_INFO_PATH);
        String ShardingInfo = JSON.toJSONString(shardingMap);
        if (stat == null) {
            // SHARDING_INFO_PATH设为临时节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .forPath(SHARDING_INFO_PATH, ShardingInfo.getBytes());
        } else {
            client.setData().forPath(SHARDING_INFO_PATH, ShardingInfo.getBytes());
        }

    }


    /**
     * master把选举状态信息写入zk
     * @param client
     * @Param isFinish 是否选举完成
     * @throws Exception
     */
    public void masterClaimElectionStatus(CuratorFramework client, boolean isFinish) throws Exception {
        ElectionStatusType electionStatusType;
        if (isFinish) {
            electionStatusType = ElectionStatusType.FINISH;
        } else {
            electionStatusType = ElectionStatusType.NOT_YET;
        }

        Stat stat = client.checkExists().forPath(ELECTION_STATUS_PATH);
        if (stat == null) {
            // ELECTION_STATUS_PATH设为临时节点
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ELECTION_STATUS_PATH,
                electionStatusType.getValue().toString().getBytes());
        } else {
            client.setData().forPath(ELECTION_STATUS_PATH,
                electionStatusType.getValue().toString().getBytes());
        }
    }

    /**
     * master更新本机选举状态
     * @Param isFinish 是否选举完成
     */
    public void masterUpdateSelfStatus(boolean isFinish) {
        electionStatus.setElectionFinish(ElectionStatusType.FINISH);
        if (isFinish) {
            logger.info("*** Election finish. I am master. ***");
        } else {
            logger.info("*** Re-election finish. I am master. ***");
        }
    }

    /**
     * 成为master后，可能master是由slave转变而成的
     * 一部分zk目录是由master创建，master无需在监控这些节点，所以需要关闭之前作为slave
     * 而创建的listener
     */
    public void masterCloseSlaveListener() throws Exception {
        if (electionListener.getSlaveMonitorShardingInfoListener() != null) {
            electionListener.getSlaveMonitorShardingInfoListener().close();
        }

        if (electionListener.getSlaveMonitorElectionStatusListener() != null) {
            electionListener.getSlaveMonitorElectionStatusListener().close();
        }
    }
}
