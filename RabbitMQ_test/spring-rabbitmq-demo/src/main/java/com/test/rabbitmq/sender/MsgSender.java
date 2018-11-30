
package com.test.rabbitmq.sender;

import java.util.Random;
import java.util.UUID;

import com.test.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MsgSender implements ConfirmCallback, ReturnCallback, InitializingBean {
	/**
	 * Rabbit MQ 客户端
	 */
	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 每2秒钟发送一个随机数字到队列
	 */
	@Scheduled(cron = "0/10 * * * * ?")
	public void send() {
		try {
			CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
			int data = new Random().nextInt(500);
			System.out.println(String.format("发送消息：%s, id: %s", data, correlationData.getId()));

			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_MSG, data
					, correlationData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		// 当消息发送出去找不到对应路由队列时，将会把消息退回
		// 如果有任何一个路由队列接收投递消息成功，则不会退回消息
		System.out.println("消息退回：" + message.getBody());
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		// ACK=true仅仅标示消息已被Broker接收到，并不表示已成功投放至消息队列中
		// ACK=false标示消息由于Broker处理错误，消息并未处理成功
		System.out.println(String.format("消息送达确认结果：%s, id: %s", ack, correlationData.getId()));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 必须设置消息送达确认的方式
		rabbitTemplate.setConfirmCallback(this);
		rabbitTemplate.setReturnCallback(this);
	}

}
