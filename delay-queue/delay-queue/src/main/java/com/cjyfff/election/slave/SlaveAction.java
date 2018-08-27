package com.cjyfff.election.slave;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;

import com.cjyfff.election.ElectionStatus;
import com.cjyfff.election.ElectionStatus.ElectionStatusType;
import com.cjyfff.election.ShardingInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-8-25.
 */
@Component
public class SlaveAction {

    private static final String SHARDING_INFO_PATH = "/sharding_info";

    private static final String ELECTION_STATUS_PATH = "/election_status";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private ElectionStatus electionStatus;

    /**
     * slave监听并保存集群分片信息
     * @param client
     * @throws Exception
     */
    public void slaveMonitorShardingInfo(CuratorFramework client) throws Exception {

        // 防止监控开始时，master还没有写入分片信息，因此循环等待
        while (client.checkExists().forPath(SHARDING_INFO_PATH) == null) {
            TimeUnit.SECONDS.sleep(1);
        }

        if (electionStatus.getLeaderLatch() == null) {
            throw new Exception("LeaderLatch in electionStatus get null.");
        }
        // 确保只有slave才执行
        if (electionStatus.getLeaderLatch().hasLeadership()) {
            return;
        }

        // 防止listener启动前，数据已经被设置，因此先读取一次数据
        byte[] bs = client.getData().forPath(SHARDING_INFO_PATH);
        if (bs != null && bs.length > 0) {
            String shardingData = new String(bs);
            logger.info("Slave get cluster sharding info: " + shardingData);
            Map<Integer, String> shardingMap = JSON.parseObject(shardingData, Map.class);
            shardingInfo.setShardingMap(shardingMap);
        }

        NodeCache cache = new NodeCache(client, SHARDING_INFO_PATH);
        NodeCacheListener listener = () -> {
            ChildData data = cache.getCurrentData();
            if (null != data) {
                String shardingData = new String(cache.getCurrentData().getData());
                logger.info("Slave get cluster sharding info changed：" + shardingData);
                Map<Integer, String> shardingMap = JSON.parseObject(shardingData, Map.class);
                shardingInfo.setShardingMap(shardingMap);

            } else {
                shardingInfo.setShardingMap(null);
                logger.info("Slave get cluster sharding info has been deleted or not exist..,");
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();
    }

    /**
     * slave监控选举状态
     * 当从zk取得的选举状态是完成时，修改本机选举状态
     * @param client
     * @throws Exception
     */
    public void slaveMonitorElectionStatus(CuratorFramework client) throws Exception {

        // 防止监控开始时，master还没有写入选举状态信息，因此循环等待
        while (client.checkExists().forPath(ELECTION_STATUS_PATH) == null) {
            TimeUnit.SECONDS.sleep(1);
        }

        if (electionStatus.getLeaderLatch() == null) {
            throw new Exception("LeaderLatch in electionStatus get null.");
        }
        // 确保只有slave才执行
        if (electionStatus.getLeaderLatch().hasLeadership()) {
            return;
        }

        // 防止listener启动前，数据已经被设置，因此先读取一次数据
        byte[] bs = client.getData().forPath(ELECTION_STATUS_PATH);
        if (bs != null && bs.length > 0) {
            Integer electionStatusValue = Integer.valueOf(new String(bs));
            logger.info("Slave get election status: " + electionStatusValue);

            if (ElectionStatusType.FINISH.getValue().equals(electionStatusValue)) {
                electionStatus.setElectionFinish(ElectionStatusType.FINISH);
                logger.info("*** Election finish. I am slave. ***");
            } else {
                electionStatus.setElectionFinish(ElectionStatusType.NOT_YET);
            }

        }

        NodeCache cache = new NodeCache(client, ELECTION_STATUS_PATH);
        NodeCacheListener listener = () -> {
            ChildData data = cache.getCurrentData();
            if (null != data) {
                Integer electionStatusValue = Integer.valueOf(new String(cache.getCurrentData().getData()));
                logger.info("Slave get election status data changed：" + electionStatusValue);

                if (ElectionStatusType.FINISH.getValue().equals(electionStatusValue)) {
                    electionStatus.setElectionFinish(ElectionStatusType.FINISH);
                    logger.info("*** Election finish. I am slave. ***");
                } else {
                    electionStatus.setElectionFinish(ElectionStatusType.NOT_YET);
                }

            } else {
                logger.info("Slave get election status data has been deleted or not exist..,");
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();
    }
}
