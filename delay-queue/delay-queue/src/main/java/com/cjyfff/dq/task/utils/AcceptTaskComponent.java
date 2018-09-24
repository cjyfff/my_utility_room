package com.cjyfff.dq.task.utils;

import java.util.Map;

import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/9/24.
 */
@Component
public class AcceptTaskComponent {

    @Autowired
    private ElectionStatus electionStatus;

    @Autowired
    private ShardingInfo shardingInfo;

    public void checkElectionStatus() throws ApiException {
        if (! ElectionStatusType.FINISH.equals(electionStatus.getElectionStatus())) {
            throw new ApiException("101", "选举未完成，不接受请求");
        }
    }

    public Integer getShardingIdFormTaskId(String taskId) {
        Map<Integer, String> shardingMap = shardingInfo.getShardingMap();

        // 分片数量
        int shardingAmount = shardingMap.size();

        return taskId.hashCode() % shardingAmount;
    }
}
