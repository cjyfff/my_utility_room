package com.example.mq_test.provider;

import java.util.Date;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/16.
 */
@Component
public class PollingTestProvider {

    @Autowired
    private Provider provider;

    @Scheduled(fixedRate = 2000)
    public void run() throws Exception {
        String context = "hello " + new Date();
        provider.send(context);
    }
}
