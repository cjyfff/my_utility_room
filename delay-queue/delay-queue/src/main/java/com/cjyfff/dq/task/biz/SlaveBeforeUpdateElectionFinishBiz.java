package com.cjyfff.dq.task.biz;

import com.cjyfff.dq.election.biz.NoneBiz;
import com.cjyfff.dq.task.component.AcceptTaskComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 18-12-11.
 */
@Component
@Slf4j
public class SlaveBeforeUpdateElectionFinishBiz extends NoneBiz{

    @Autowired
    private AcceptTaskComponent acceptTaskComponent;

    @Autowired
    private BizComponent bizComponent;

    @Override
    @Transactional
    public void run() {
        log.info("SlaveBeforeUpdateElectionFinishBiz begin...");
        acceptTaskComponent.clearQueue();

        bizComponent.rePushTaskToQueue();

        log.info("SlaveBeforeUpdateElectionFinishBiz end...");
    }
}
