package com.example.mq_test.consumer;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/16.
 */
@Slf4j
@Component
@RabbitListener(queues = "hello")
public class Consumer {
    @RabbitHandler
    public void process(@Payload String context, Channel channel, @Headers Map<String,Object> headers) {

        long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            //手工ack, 第二個參數 multiple：为了减少网络流量，手动确认可以被批处理，
            // 当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(deliveryTag, false);

            log.info("receive--1: " + context);
        } catch (Exception e) {
            log.error("-", e);
            // nack的話，消息會被rabbit mq不斷重發
            // 第二个参数是否应用于多消息，第三个参数是否requeue
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ioe) {
                log.error("ioe -", ioe);
            }
        }
    }
}
