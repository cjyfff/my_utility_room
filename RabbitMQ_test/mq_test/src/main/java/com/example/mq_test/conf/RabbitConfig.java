package com.example.mq_test.conf;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jiashen on 2020/4/16.
 */
@Configuration
public class RabbitConfig {


    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange("test");
    }

    /**
     * 消費者用的 queue 定義
     * @return
     */
    @Bean
    public Queue helloQueue() {
        return new Queue("hello");
    }

    /**
     * 消費者用的 queue 綁定
     * @return
     */
    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(helloQueue()).to(topicExchange()).with("key.1");
    }
}
