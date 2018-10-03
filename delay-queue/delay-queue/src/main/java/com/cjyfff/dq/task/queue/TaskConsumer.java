package com.cjyfff.dq.task.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
@DependsOn(value = "delayTaskQueue")
public class TaskConsumer {

    @Autowired
    private DelayTaskQueue delayTaskQueue;

    public void start() {

    }
}
