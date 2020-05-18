package com.example.mq_test.provider;

import java.util.Map.Entry;

import com.example.mq_test.vo.HistoryMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/17.
 */
@Slf4j
@Component
public class PollingResendProvider {

    @Autowired
    private Provider provider;

    @Scheduled(fixedRate = 2000)
    public void resend() {
        for (Entry<String, HistoryMsgVo> record : MsgRecord.HistoryMsgMap.entrySet()) {
            long nowMillis = System.currentTimeMillis();
            if (nowMillis - record.getValue().getSendTime() > 2000) {
                String msgId = record.getKey();
                log.info("resend msg :{}", msgId);

                provider.send(record.getValue().getContext());
            }
        }
    }
}
