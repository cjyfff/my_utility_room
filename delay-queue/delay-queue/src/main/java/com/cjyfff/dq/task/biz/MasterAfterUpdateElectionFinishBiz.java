package com.cjyfff.dq.task.biz;

import java.util.List;

import com.cjyfff.dq.election.biz.ElectionBiz;
import com.cjyfff.dq.election.info.ShardingInfo;
import com.cjyfff.dq.task.common.SysStatus;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.common.component.AcceptTaskComponent;
import com.cjyfff.dq.task.common.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * master选举完成后执行的逻辑：
 * 1、初次启动，重启后把数据库中`队列中`状态的任务加载到队列，防止重启后内存队列中的数据丢失
 * Created by jiashen on 18-11-23.
 */
@Component
@Slf4j
public class MasterAfterUpdateElectionFinishBiz implements ElectionBiz {

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ShardingInfo shardingInfo;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private ExecLogComponent execLogComponent;

    @Autowired
    private SysStatus sysStatus;

    @Override
    @Transactional
    public void run() throws Exception {
        log.info("MasterAfterUpdateElectionFinishBiz begin...");
        try {
            if (sysStatus.isInitCompleted()) {
                log.info("MasterAfterUpdateElectionFinishBiz end...");
                return;
            }

            log.info("Begin pushTaskInQueueWhenInit...");

            List<DelayTask> delayTaskList = delayTaskMapper.selectByStatusAndShardingIdForUpdate(
                TaskStatus.IN_QUEUE.getStatus(),
                shardingInfo.getNodeId().byteValue());

            for (DelayTask delayTask : delayTaskList) {
                QueueTask task = new QueueTask(
                    delayTask.getTaskId(), delayTask.getFunctionName(), delayTask.getParams(),
                    delayTask.getExecuteTime()
                );
                acceptTaskComponent.pushToQueue(task);

                execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
                    String.format("push task in queue when init: %s", delayTask.getTaskId()));
            }

            if (!sysStatus.isInitCompleted()) {
                sysStatus.setInitCompleted(true);
            }
        } catch (Exception e) {
            log.error("MasterBeforeUpdateElectionFinishBiz get error:", e);
        }
        log.info("MasterAfterUpdateElectionFinishBiz end...");
    }
}
