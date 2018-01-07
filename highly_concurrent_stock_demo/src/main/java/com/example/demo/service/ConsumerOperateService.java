package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 功能点
 * 1、消费者初始化
 * 2、控制消费者切换
 * @author jiashen
 * @date 17-9-16
 */
@Service
public class ConsumerOperateService {

    /**
     * 消费者对象确保是单例
     */
    private Object dbConsumer;

    private Object cacheConsumer;

    public ConsumerOperateService() {
        // set this.dbConsumer, this.cacheConsumer
    }

    /**
     * 功能点
     * 1、停止dbConsumer
     * 2、数据从db迁移到cache、cache数据设置一个过期时间
     * 3、启动cacheConsumer
     */
    @Transactional
    public void begin() {}

    /**
     * 功能点
     * 1、停止cacheConsumer
     * 2、数据从cache迁移到db、cache数据不用清空，等它自己过期
     * 3、启动dbConsumer
     */
    @Transactional
    public void stop() {}
}
