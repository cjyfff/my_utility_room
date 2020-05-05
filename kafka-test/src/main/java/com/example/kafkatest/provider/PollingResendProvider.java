package com.example.kafkatest.provider;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.example.kafkatest.vo.HistoryMsgVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/4/23.
 */
@Slf4j
@Component
public class PollingResendProvider {
    @Autowired
    private Provider provider;

    @Scheduled(fixedRate = 2000)
    public void resend() {
        Set<Entry<String, HistoryMsgVo>> set = MsgRecord.HistoryMsgMap.entrySet();
        Iterator<Entry<String, HistoryMsgVo>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Entry<String, HistoryMsgVo> record = iterator.next();

            long nowMillis = System.currentTimeMillis();
            if (nowMillis - record.getValue().getSendTime() > 2000) {
                String msgId = record.getKey();
                log.info("resend msg :{}", msgId);

                // 要記得先刪除舊紀錄
                iterator.remove();
                provider.send(record.getValue().getContext());
            }
        }
    }
}
