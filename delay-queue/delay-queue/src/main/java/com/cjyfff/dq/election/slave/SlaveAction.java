package com.cjyfff.dq.election.slave;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.cjyfff.dq.biz.NoneBusinessLogic;
import com.cjyfff.dq.election.info.ElectionListener;
import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.dq.election.ElectionComponent;
import com.cjyfff.dq.election.info.SetSelfESAndRunBLProxy;
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
    private ElectionStatus electionStatus;

    @Autowired
    private ElectionListener electionListener;

    @Autowired
    private ElectionComponent electionComponent;

    @Autowired
    private SetSelfESAndRunBLProxy setSelfESAndRunBLProxy;

    /**
     * slave监听并保存集群分片信息
     * @param client
     * @throws Exception
     */
    public void slaveMonitorShardingInfo(CuratorFramework client) throws Exception {

        NodeCache cache = new NodeCache(client, SHARDING_INFO_PATH);
        NodeCacheListener listener = () -> {
            ChildData data = cache.getCurrentData();
            if (null != data) {
                String shardingData = new String(cache.getCurrentData().getData());
                logger.info("Slave get cluster sharding info changed：" + shardingData);
                Map<Integer, String> shardingMap = JSON.parseObject(shardingData, Map.class);
                electionComponent.updateSelfShardingInfo(shardingMap);

            } else {
                electionComponent.updateSelfShardingInfo(null);
                logger.info("Slave get cluster sharding info has been deleted or not exist..,");
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();
        electionListener.setSlaveMonitorShardingInfoListener(cache);
    }

    /**
     * slave监控选举状态
     * 当从zk取得的选举状态是完成时，修改本机选举状态
     * @param client
     * @throws Exception
     */
    public void slaveMonitorElectionStatus(CuratorFramework client) throws Exception {

        NodeCache cache = new NodeCache(client, ELECTION_STATUS_PATH);
        NodeCacheListener listener = () -> {
            ChildData data = cache.getCurrentData();
            if (null != data) {
                Integer electionStatusValue = Integer.valueOf(new String(cache.getCurrentData().getData()));
                logger.info("Slave get election status data changed：" + electionStatusValue);

                if (ElectionStatusType.FINISH.getValue().equals(electionStatusValue)) {
                    setSelfESAndRunBLProxy.setFinish(new NoneBusinessLogic(), new NoneBusinessLogic());
                    logger.info("*** Election finish. I am slave. ***");
                } else {
                    setSelfESAndRunBLProxy.setNotYet(new NoneBusinessLogic(), new NoneBusinessLogic());
                }

            } else {
                setSelfESAndRunBLProxy.setNotYet(new NoneBusinessLogic(), new NoneBusinessLogic());
                logger.info("Slave get election info data has been deleted or not exist..,");
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();
        electionListener.setSlaveMonitorElectionStatusListener(cache);
    }
}
