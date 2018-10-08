package com.cjyfff.dq.election.master;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.biz.MasterBLAfterElectionFinish;
import com.cjyfff.dq.biz.NoneBusinessLogic;
import com.cjyfff.dq.election.info.ElectionListener;
import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.dq.election.ElectionComponent;
import com.cjyfff.dq.election.info.SetSelfESAndRunBLProxy;
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

    @Autowired
    private ElectionListener electionListener;

    @Autowired
    private ElectionComponent electionComponent;

    @Autowired
    private SetSelfESAndRunBLProxy setSelfESAndRunBLProxy;

    @Autowired
    private MasterBLAfterElectionFinish masterBLAfterElectionFinish;

    /**
     * master统计节点，分配node id，写入zk
     * 同时更新本机节点信息
     * @param client
     */
    public void masterSetShardingInfo(CuratorFramework client) throws Exception {
        List<String> nodeHostList = client.getChildren().forPath(NODE_INFO_PATH);
        Integer nodeId = 0;
        Map<Integer, String> shardingMap = Maps.newHashMapWithExpectedSize(15);
        for (String nodeHost : nodeHostList) {
            shardingMap.put(nodeId, nodeHost);
            logger.info("Host: " + nodeHost + ", get nodeId: " + nodeId);
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

        electionComponent.updateSelfShardingInfo(shardingMap);
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
    public void masterUpdateSelfStatus(boolean isFinish) throws Exception {

        if (isFinish) {
            logger.info("*** Election finish. I am master. ***");
            setSelfESAndRunBLProxy.setFinish(new NoneBusinessLogic(), masterBLAfterElectionFinish);
        } else {
            setSelfESAndRunBLProxy.setNotYet(new NoneBusinessLogic(), new NoneBusinessLogic());
        }

    }

    /**
     * 成为master后，可能master是由slave转变而成的
     * 一部分zk目录是由master创建，master无需再监控这些节点，所以需要关闭之前作为slave
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

    /**
     * master 更新 zk 与自身的选举状态
     * @throws Exception
     */
    public void masterUpdateZkAndSelfElectionStatus(CuratorFramework client, boolean isFinish) throws Exception {
        if (isFinish) {
            masterClaimElectionStatus(client, true);

            // todo: 处理本机设置选举成功后，node info change listener才回调导致2次分片的问题
            masterUpdateSelfStatus(true);
        } else {
            masterClaimElectionStatus(client, false);

            masterUpdateSelfStatus(false);
        }
    }
}
