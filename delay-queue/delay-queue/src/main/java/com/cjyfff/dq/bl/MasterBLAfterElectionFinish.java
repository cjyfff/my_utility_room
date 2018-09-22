package com.cjyfff.dq.bl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 当选为master后需要先进行的业务逻辑
 * 目前需要进行的处理有：
 * 1、数据库中的重新分片
 * Created by jiashen on 2018/9/9.
 */
@Component
public class MasterBLAfterElectionFinish implements BusinessLogic {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() throws Exception {
        logger.info("Master begin process business logic...");
        TimeUnit.SECONDS.sleep(5);
        logger.info("Master finish processing business logic.");
    }
}
