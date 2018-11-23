package com.cjyfff.dq.task.service.impl;

import java.util.Date;

import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.ApiException;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
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
    private ExecLogComponent execLogComponent;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(InnerMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        if (acceptTaskComponent.checkNeedToPushQueueNow(reqDto.getDelayTime())) {
            DelayTask delayTask = delayTaskMapper.selectByTaskIdAndStatusForUpdate(
                TaskStatus.TRANSMITING.getStatus(), reqDto.getTaskId(),
                shardingInfo.getNodeId().byteValue());
            if (delayTask == null) {
                throw new ApiException("-101",
                    String.format("Can not find task by task id: %s", reqDto.getTaskId()));
            }

            QueueTask task = new QueueTask(
                reqDto.getTaskId(), reqDto.getFunctionName(), reqDto.getParams(),
                reqDto.getDelayTime()
            );
            acceptTaskComponent.pushToQueue(task);
            delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
            delayTask.setModifiedAt(new Date());
            delayTaskMapper.updateByPrimaryKeySelective(delayTask);

            execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                String.format("In Queue: %s", delayTask.getTaskId()));
        }
    }
}
