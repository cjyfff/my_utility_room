
package com.test.rabbitmq.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig implements InitializingBean {
	/**
	 * 交换器名字
	 */
	public static final String EXCHANGE_NAME = "exchange.log";
	/**
	 * 队列名字
	 */
	public static final String QUEUE_NAME_LOG = "queue.log";
	/**
	 * 队列名字
	 */
	public static final String QUEUE_NAME_MSG = "queue.msg";
	/**
	 * 队列名字
	 */
	public static final String QUEUE_NAME_ERROR = "queue.error";
	/**
	 * 路由Key
	 */
	public static final String ROUTING_KEY_LOG = "routing.*";
	/**
	 * 路由Key
	 */
	public static final String ROUTING_KEY_MSG = "routing.msg.*";
	/**
	 * 路由Key
	 */
	public static final String ROUTING_KEY_ERROR = "error.*";
	/**
	 * MQ管理代理
	 */
	@Autowired
	private AmqpAdmin amqpAdmin;

	@Override
	public void afterPropertiesSet() throws Exception {
		DirectExchange exchange = new DirectExchange(EXCHANGE_NAME, true, false);
		//Queue queueLog = new Queue(QUEUE_NAME_LOG);
		Queue queueMsg = new Queue(QUEUE_NAME_MSG, true);
		//Queue queueError = new Queue(QUEUE_NAME_ERROR);
		amqpAdmin.declareExchange(exchange);
		//amqpAdmin.declareQueue(queueLog);
		amqpAdmin.declareQueue(queueMsg);
		//amqpAdmin.declareQueue(queueError);
		//amqpAdmin.declareBinding(BindingBuilder.bind(queueLog).to(exchange).with(ROUTING_KEY_LOG));
		amqpAdmin.declareBinding(BindingBuilder.bind(queueMsg).to(exchange).with(ROUTING_KEY_MSG));
		//amqpAdmin.declareBinding(BindingBuilder.bind(queueError).to(exchange).with(ROUTING_KEY_ERROR));
	}
}
