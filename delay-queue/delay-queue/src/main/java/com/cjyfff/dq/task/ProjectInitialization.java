package com.cjyfff.dq.task;

import javax.annotation.PostConstruct;

import com.cjyfff.dq.election.Election;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2018/10/3.
 */
@Component
public class ProjectInitialization {
    @Autowired
    private Election election;

    @PostConstruct
    public void init() throws Exception {
        election.start();
    }
}
