package com.example.netty_ws_test.sys;

import com.example.netty_ws_test.service.impl.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/3/31.
 */
@Component
public class ProjectInitialization implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectInitialization.class);

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            nettyServer.start();
        } catch (Exception e) {
            log.error("-:", e);
        }
    }
}
