package com.cjyfff.dq.task.biz;

import com.cjyfff.dq.election.biz.ElectionBiz;
import com.cjyfff.dq.task.biz.component.CommonBiz;
import com.cjyfff.dq.task.common.SysStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * slave选举完成后执行的逻辑：
 * 1、初次启动，重启后把数据库中`队列中`状态的任务加载到队列，防止重启后内存队列中的数据丢失
 * Created by jiashen on 18-12-11.
 */
@Component
@Slf4j
public class SlaveAfterUpdateElectionFinishBiz implements ElectionBiz {
    @Autowired
    private CommonBiz commonBiz;

    @Autowired
    private SysStatus sysStatus;

    @Override
    public void run() throws Exception {
        log.info("SlaveAfterUpdateElectionFinishBiz begin...");
        try {
            commonBiz.pushTaskInQueueWhenInit();

            if (!sysStatus.isInitCompleted()) {
                sysStatus.setInitCompleted(true);
            }
        } catch (Exception e) {
            log.error("SlaveAfterUpdateElectionFinishBiz get error:", e);
        }
        log.info("SlaveAfterUpdateElectionFinishBiz end...");
    }
}
