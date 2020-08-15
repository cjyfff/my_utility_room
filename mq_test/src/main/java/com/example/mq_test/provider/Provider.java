package com.example.mq_test.provider;

import java.util.UUID;

import com.example.mq_test.vo.HistoryMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/16.
 */
@Slf4j
@Component
public class Provider implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback, InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void send(String context) {
        log.info("Provider : " + context);
        String msgId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(msgId);
        log.info("callbackSender UUID: " + msgId);
        this.rabbitTemplate.convertAndSend("test","key.1", context, correlationData);

        HistoryMsgVo historyMsgVo = new HistoryMsgVo(System.currentTimeMillis(), context);
        MsgRecord.HistoryMsgMap.put(msgId, historyMsgVo);
    }

    /**
     * 這個消息確認與消費者是否確認無關
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("收到rabbit mq消息確認回調correlationData: {}, cause: {}", correlationData, cause);
        if (ack) {

            log.info("消息: "+correlationData+"，已经被ack成功");

            if (correlationData != null) {
                String msgId = correlationData.getId();
                if (msgId != null) {
                    MsgRecord.HistoryMsgMap.remove(msgId);
                }
            }
        } else {
            log.info("消息: "+correlationData+"，失敗，失败原因是："+cause);
        }
    }

    /**
     * 投遞時沒有創建對應交換機時，報錯回調
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("sender return fail" + message.toString());
    }

    @Override
    public void afterPropertiesSet() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }
}
