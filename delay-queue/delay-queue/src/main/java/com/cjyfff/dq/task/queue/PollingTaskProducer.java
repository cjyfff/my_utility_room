package com.cjyfff.dq.task.queue;

import java.util.Date;
import java.util.List;

import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2018/10/4.
 * 从数据库中取出任务插入延时队列的定时任务
 */
@Component
@Slf4j
public class PollingTaskProducer {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Value("delay_queue.critical_polling_time")
    private Long pollingTime;

    @Scheduled(fixedRateString = "#{${delay_queue.critical_polling_time} * 1000}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("begin PollingTaskProducer ------");
        // 根据状态、sharding id、执行时间select for update查出数据,
        // 入队，然后update状态（无需再根据状态筛选来更新）

        Long nowSecond = System.currentTimeMillis() / 1000;
        List<DelayTask> taskList = delayTaskMapper.selectByStatusAndExecuteTimeForUpdate(TaskStatus.ACCEPT.getStatus(),
            shardingInfo.getNodeId().byteValue(), nowSecond - pollingTime, nowSecond);

        for (DelayTask delayTask : taskList) {
            QueueTask task = new QueueTask(
                delayTask.getTaskId(), delayTask.getFunctionName(), delayTask.getParams(),
                delayTask.getDelayTime()
            );
            acceptTaskComponent.pushToQueue(task);

            delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
            delayTask.setModifiedAt(new Date());
            delayTaskMapper.updateByPrimaryKeySelective(delayTask);

            execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                String.format("polling task in queue: %s", delayTask.getTaskId()));
        }


    }
}
