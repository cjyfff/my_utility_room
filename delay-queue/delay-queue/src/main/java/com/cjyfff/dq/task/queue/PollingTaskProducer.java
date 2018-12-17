package com.cjyfff.dq.task.queue;

import java.util.Date;
import java.util.List;

import com.cjyfff.dq.config.ZooKeeperClient;
import com.cjyfff.dq.election.info.ElectionStatus;
import com.cjyfff.dq.election.info.ElectionStatus.ElectionStatusType;
import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.TaskConfig;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.common.lock.ZkLock;
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
    private ElectionStatus electionStatus;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Autowired
    private ZkLock zkLock;

    @Autowired
    private ZooKeeperClient zooKeeperClient;

    @Value("${delay_queue.critical_polling_time}")
    private Long pollingTime;

    @Scheduled(fixedRateString = "#{${delay_queue.critical_polling_time} * 1000}")
    @Transactional(rollbackFor = Exception.class)
    public void run() throws Exception {
        log.info("begin PollingTaskProducer ------");

        if (! ElectionStatusType.FINISH.equals(electionStatus.getElectionStatus())) {
            log.warn("Election not finish, PollingTaskProducer can not process...");
            return;
        }

        // 根据状态、sharding id、执行时间select for update查出数据,
        // 入队，然后update状态（无需再根据状态筛选来更新）

        Long nowSecond = System.currentTimeMillis() / 1000;
        List<DelayTask> taskList = delayTaskMapper.selectByStatusAndExecuteTimeForUpdate(TaskStatus.ACCEPT.getStatus(),
            shardingInfo.getNodeId().byteValue(), nowSecond, nowSecond + pollingTime);

        for (DelayTask delayTask : taskList) {
            if (zkLock.idempotentLock(zooKeeperClient.getClient(), TaskConfig.IN_QUEUE_LOCK_PATH, delayTask.getTaskId())) {
                try {
                    QueueTask task = new QueueTask(
                        delayTask.getTaskId(), delayTask.getFunctionName(), delayTask.getParams(),
                        delayTask.getExecuteTime()
                    );
                    acceptTaskComponent.pushToQueue(task);

                    delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
                    delayTask.setModifiedAt(new Date());
                    delayTaskMapper.updateByPrimaryKeySelective(delayTask);

                    execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                        String.format("polling task in queue: %s", delayTask.getTaskId()));
                } catch (Exception e) {
                    // 入队列锁只在队列执行后才解锁，此处不应解锁，除非入队列，写操作日志过程中出现异常
                    zkLock.tryUnlock(TaskConfig.IN_QUEUE_LOCK_PATH, delayTask.getTaskId());
                    throw e;
                }

            } else {
                log.error(String.format("Task can not get in queue lock : %s", delayTask.getTaskId()));
                execLogComponent.insertLog(delayTask, TaskStatus.ACCEPT.getStatus(),
                    String.format("Task can not get in queue lock : %s", delayTask.getTaskId()));
            }
        }


    }
}
