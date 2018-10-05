package com.cjyfff.dq.task.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
@Slf4j
public class TaskConsumer {

    @Autowired
    private DelayTaskQueue delayTaskQueue;

    /**
     * delay queue consumer
     * 对于这个consumer，实际只需要一个线程，
     * 队列非空时，take方法会阻塞直到队列第一个元素的时间达到，
     * 加入阻塞期间有一个时间更短的元素插入，队列会自动消费更前的那个元素。
     * 不过假如消费过程比较耗时，多个相同时间的元素还是不会同时消费，这种情况应该把消费逻辑放到异步队列中处理
     * @throws Exception
     */
    @Scheduled(fixedDelay = 1000)
    public void consumer() throws Exception {
        log.info("---------------");
        // 这里必须用死循环来取元素，
        // 假如通过Scheduled来控制取元素频率的话
        // 两个任务的时间相隔小于Scheduled delay time时，也会变为相隔Scheduled delay time
        while (! delayTaskQueue.queue.isEmpty()) {
            QueueTask task = delayTaskQueue.queue.take();
            log.info(String.format("task %s begin", task.getTaskId()));
            // 1、乐观锁更新状态
            // 2、用task id 查出数据
            // 3、处理
            // 4、修改状态
        }
    }
}
