
package com.test.rabbitmq.receiver;

import java.util.concurrent.TimeUnit;

import com.test.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

@Service
public class MsgAckReceiver {
	/**
	 * 消息队列，只接收消息内容
	 * 
	 */
	@RabbitListener(queues = RabbitMQConfig.QUEUE_NAME_MSG)
	public void receivMsg(int msg, String lmsg, Message message, Channel channel) {
		System.out.println("MsgAckReceiver1 收到消息：" + msg + "," + lmsg);

		// 确认消息已经消费成功
		try {

			TimeUnit.SECONDS.sleep(10);

			// 确认成功
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			// 确认失败
			//channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
