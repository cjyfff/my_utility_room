package com.cjyfff.dq.biz;

import java.util.concurrent.TimeUnit;

import com.cjyfff.dq.election.biz.ElectionBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 因节点变更而当选为master，在宣告选举完成之前需要先进行的业务逻辑
 * 目前需要进行的处理有：
 * 1、数据库中的重新分片
 * Created by jiashen on 2018/9/9.
 */
@Component
public class MasterBeforeUpdateElectionFinishBiz implements ElectionBiz {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() throws Exception {
        logger.debug("MasterBeforeElectionFinishBiz begin...");
        TimeUnit.SECONDS.sleep(5);
        logger.debug("MasterBeforeElectionFinishBiz end...");
    }
}
