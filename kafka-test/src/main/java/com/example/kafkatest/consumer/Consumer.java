package com.example.kafkatest.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/21.
 */
@Component
@Slf4j
public class Consumer {
    /**
     * @KafkaListener 可以指定消費的分區
     * @param message
     */
    @KafkaListener(topics = "cjyffftest")
    public void listen(ConsumerRecord<?, ?> message, Acknowledgment ack){
        log.info(message.toString());

        ack.acknowledge();
    }
}
