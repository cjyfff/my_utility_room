package com.cjyfff.dq.task;

import javax.annotation.PostConstruct;

import com.cjyfff.dq.election.Election;
import com.cjyfff.dq.task.queue.TaskConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
public class ProjectInitialization {
    @Autowired
    private Election election;

    @Autowired
    private TaskConsumer taskConsumer;

    @PostConstruct
    public void init() {
        election.start();

        taskConsumer.start();
    }
}
