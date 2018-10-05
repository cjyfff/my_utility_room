package com.cjyfff.dq.task.queue;

import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(fixedRateString = "#{${delay_queue.critical_polling_time} * 1000}")
    @Transactional(rollbackFor = Exception.class)
    public void run() {
        log.info("begin PollingTaskProducer ------");
        // 根据状态、sharding id、执行时间select for update查出数据,
        // 入队，然后update状态（无需再根据状态筛选来更新）
    }
}
