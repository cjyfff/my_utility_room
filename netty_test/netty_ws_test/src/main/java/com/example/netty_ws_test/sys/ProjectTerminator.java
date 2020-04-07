package com.example.netty_ws_test.sys;

import com.example.netty_ws_test.service.impl.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/3/31.
 */
@Component
public class ProjectTerminator implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LoggerFactory.getLogger(ProjectTerminator.class);

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            if (nettyServer.getBossGroup() != null) {
                nettyServer.getBossGroup().shutdownGracefully().sync();
            }
            if (nettyServer.getWorkGroup() != null) {
                nettyServer.getWorkGroup().shutdownGracefully().sync();
            }
        } catch (Exception e) {
            log.error("-: ", e);
        }
    }
}
