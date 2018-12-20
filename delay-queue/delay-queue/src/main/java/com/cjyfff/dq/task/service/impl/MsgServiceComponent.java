package com.cjyfff.dq.task.service.impl;

import java.util.Date;

import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.component.ExecLogComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import com.cjyfff.dq.task.queue.QueueTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 18-12-20.
 */
@Component
public class MsgServiceComponent {

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private ExecLogComponent execLogComponent;

    public void doPush2Queue(DelayTask delayTask) {
        QueueTask task = new QueueTask(
            delayTask.getTaskId(), delayTask.getFunctionName(), delayTask.getParams(),
            delayTask.getExecuteTime()
        );
        acceptTaskComponent.pushToQueue(task);
        delayTask.setStatus(TaskStatus.IN_QUEUE.getStatus());
        delayTask.setModifiedAt(new Date());
        delayTaskMapper.updateByPrimaryKeySelective(delayTask);

        execLogComponent.insertLog(delayTask, TaskStatus.IN_QUEUE.getStatus(),
            String.format("In Queue: %s", delayTask.getTaskId()));
    }

    public void doPush2Polling(DelayTask delayTask) {
        delayTask.setStatus(TaskStatus.POLLING.getStatus());
        delayTask.setModifiedAt(new Date());
        delayTaskMapper.updateByPrimaryKeySelective(delayTask);
    }
}
