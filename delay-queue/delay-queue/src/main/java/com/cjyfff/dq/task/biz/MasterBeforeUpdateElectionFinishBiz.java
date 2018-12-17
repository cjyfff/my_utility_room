package com.cjyfff.dq.task.biz;

import java.util.List;
import com.cjyfff.dq.election.biz.ElectionBiz;
import com.cjyfff.dq.task.common.enums.TaskStatus;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import com.cjyfff.dq.task.mapper.DelayTaskMapper;
import com.cjyfff.dq.task.model.DelayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 当选为master，在宣告选举完成之前需要先进行的业务逻辑
 * 目前需要进行的处理有：
 * 1、数据库中的重新分片
 * Created by jiashen on 2018/9/9.
 */
@Component
public class MasterBeforeUpdateElectionFinishBiz implements ElectionBiz {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DelayTaskMapper delayTaskMapper;

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Override
    @Transactional
    public void run() throws Exception {
        logger.info("MasterBeforeElectionFinishBiz begin...");
        try {
            List<DelayTask> delayTasks = delayTaskMapper.selectByStatusForUpdate(TaskStatus.ACCEPT.getStatus());

            for (DelayTask delayTask : delayTasks) {
                Byte newShardingId = acceptTaskComponent.getShardingIdFormTaskId(delayTask.getTaskId()).byteValue();
                if (! newShardingId.equals(delayTask.getShardingId())) {
                    delayTask.setShardingId(newShardingId);
                    delayTaskMapper.updateByPrimaryKeySelective(delayTask);
                }
            }
        } catch (Exception e) {
            logger.error("MasterBeforeUpdateElectionFinishBiz get error:", e);
        }
        logger.info("MasterBeforeElectionFinishBiz end...");
    }

}
