package com.cjyfff.dq.biz;

import java.util.concurrent.TimeUnit;

import com.cjyfff.dq.election.biz.ElectionBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当选为master，在宣告选举完成之前需要先进行的业务逻辑
 * 目前需要进行的处理有：
 * 1、数据库中的重新分片
 * Created by jiashen on 2018/9/9.
 */
public class MasterBeforeUpdateElectionFinishBiz implements ElectionBiz {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() throws Exception {
        logger.info("MasterBeforeElectionFinishBiz begin...");
        TimeUnit.SECONDS.sleep(5);
        logger.info("MasterBeforeElectionFinishBiz end...");
    }
}
