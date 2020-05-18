package com.example.kafkatest.provider;

import java.util.UUID;

import com.example.kafkatest.vo.HistoryMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by jiashen on 2020/4/21.
 */
@Slf4j
@Component
public class Provider {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public void send(String msg) {
        String msgId = UUID.randomUUID().toString();
        // 可以指定發到哪個分區
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("cjyffftest", msgId, msg);

        future.addCallback(this::handlerSuccessRecord, this::handlerFailRecord);
        HistoryMsgVo historyMsgVo = new HistoryMsgVo(System.currentTimeMillis(), msg);
        MsgRecord.HistoryMsgMap.put(msgId, historyMsgVo);
    }

    private void handlerSuccessRecord(SendResult<String, Object> success) {
        log.info("KafkaMessageProducer 发送消息成功！");
        if (success != null) {
            String msgId = success.getProducerRecord().key();
            log.info("success id: " + msgId);
            MsgRecord.HistoryMsgMap.remove(msgId);
        }
    }

    private void handlerFailRecord(Throwable e) {
        log.error("KafkaMessageProducer 发送消息失败！, " + e.getMessage());
    }
}
