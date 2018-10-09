package com.cjyfff.dq.task.service.impl;

import java.util.Date;

import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import com.cjyfff.dq.task.service.PublicMsgService;
import com.cjyfff.dq.task.vo.dto.AcceptMsgDto;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/9/23.
 */
@Service
public class PublicMsgServiceImpl implements PublicMsgService {

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Value("${delay_queue.critical_polling_time}")
    private Long criticalPollingTime;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptMsg(AcceptMsgDto reqDto) throws Exception {
        acceptTaskComponent.checkElectionStatus();

        DelayTask newDelayTask = createTask(reqDto);

        if (acceptTaskComponent.checkIsMyTask(reqDto.getTaskId())) {
            if (checkNeedToPushQueueNow(reqDto.getDelayTime())) {
                QueueTask task = new QueueTask(
                    reqDto.getTaskId(), reqDto.getFunctionName(), reqDto.getParams(),
                    reqDto.getDelayTime()
                );
                acceptTaskComponent.pushToQueue(task);
                newDelayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
                newDelayTask.setModifiedAt(new Date());
                delayTaskMapper.updateByPrimaryKeySelective(newDelayTask);

                execLogComponent.insertLog(newDelayTask, TaskStatus.IN_QUEUE.getStatus(),
                    String.format("In Queue: %s", newDelayTask.getTaskId()));

            }
        } else {
            // 转发到对应机器
        }

    }

    /**
     * 根据入参中的delayTime判断任务是不是需要马上入队
     * @param delayTime
     * @return
     */
    private boolean checkNeedToPushQueueNow(Long delayTime) {
        return delayTime.compareTo(criticalPollingTime) <= 0;
    }


    /**
     * 任务持久化
     * 此处新开事务，外部回滚不影响此方法创建的数据
     * @param reqDto
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private DelayTask createTask(AcceptMsgDto reqDto) {
        DelayTask delayTask = new DelayTask();
        int ranInt = (int)(Math.random() * 90000) + 10000;

        final String finalTaskId = reqDto.getTaskId() + "-" + ranInt;
        delayTask.setTaskId(finalTaskId);
        delayTask.setFunctionName(reqDto.getFunctionName());
        delayTask.setParams(reqDto.getParams());
        delayTask.setRetryCount(reqDto.getRetryCount());
        delayTask.setRetryInterval(reqDto.getRetryInterval());
        delayTask.setDelayTime(reqDto.getDelayTime());
        delayTask.setExecuteTime(System.currentTimeMillis() / 1000 + reqDto.getDelayTime());
        delayTask.setStatus(TaskStatus.ACCEPT.getStatus());
        delayTask.setCreatedAt(new Date());
        delayTask.setModifiedAt(new Date());
        delayTask.setShardingId(acceptTaskComponent.getShardingIdFormTaskId(finalTaskId).byteValue());

        delayTaskMapper.insert(delayTask);

        execLogComponent.insertLog(delayTask, TaskStatus.ACCEPT.getStatus(), String.format("Insert task: %s", finalTaskId));

        return delayTask;
    }
}
