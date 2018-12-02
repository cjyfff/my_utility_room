package com.test.rabbitmq.receiver;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.test.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 18-11-30.
 */
@Service
public class MsgAckReceiver2 {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_MSG)
    public void receivMsg(int msg, String lmsg, Message message, Channel channel) {
        System.out.println("MsgAckReceiver2 收到消息：" + msg + "," + lmsg);

        // 确认消息已经消费成功
        try {
            // 确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 确认失败
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException ie) {
                ie.printStackTrace();
            }

        }
    }
}
