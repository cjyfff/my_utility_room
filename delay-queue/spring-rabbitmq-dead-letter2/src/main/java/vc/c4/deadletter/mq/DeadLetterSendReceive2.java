package vc.c4.deadletter.mq;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vc.c4.deadletter.config.MQConfig;
import vc.c4.deadletter.domain.ExampleObject;

/**
 * Created by jiashen on 18-7-27.
 */
@Component
public class DeadLetterSendReceive2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterSendReceive.class);

    @Autowired
    @Qualifier("outgoingSender2")
    private RabbitTemplate outgoingSender2;

    // Scheduled task to send an object every 5 seconds
    //@Scheduled(fixedDelay = 10000)
    public void sender() {
        ExampleObject ex = new ExampleObject();
        ex.setName(UUID.randomUUID().toString());
        LOGGER.info("Sending example object: " + ex.getName() + ", at: " + ex.getDate());
        outgoingSender2.convertAndSend(ex);
    }

    // 1、两个消费者，不会重复处理
    // 2、出现异常会怎样
    // 3、可以设置重试吗
    @RabbitListener(queues = MQConfig.INCOMING_QUEUE)
    public void handleMessage(ExampleObject exampleObject) {
        //int a = 1 / 0;
        LOGGER.info("Received incoming object name: " + exampleObject.getName()
            + ", now is :" + new Date());
    }
}
