package com.cjyfff.dq.task.service.impl;


import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.common.component.AcceptTaskComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.service.InnerMsgService;
import com.cjyfff.dq.task.vo.dto.InnerMsgDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 18-10-9.
 */
@Slf4j
@Service
public class InnerMsgServiceImpl implements InnerMsgService {

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private MsgServiceComponent msgServiceComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(InnerMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        DelayTask delayTask = delayTaskMapper.selectByTaskIdAndStatus(
            TaskStatus.TRANSMITING.getStatus(), reqDto.getTaskId(), shardingInfo.getNodeId().byteValue());
        if (delayTask == null) {
            throw new ApiException("-101",
                String.format("Can not find task by task id: %s", reqDto.getTaskId()));
        }

        if (acceptTaskComponent.checkNeedToPushQueueNow(reqDto.getDelayTime())) {
            msgServiceComponent.doPush2Queue(delayTask);
        } else {
            msgServiceComponent.doPush2Polling(delayTask);
        }
    }
}
