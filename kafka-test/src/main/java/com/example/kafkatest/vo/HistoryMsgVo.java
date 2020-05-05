package com.example.kafkatest.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jiashen on 2020/4/23.
 */
@Getter
@Setter
public class HistoryMsgVo {
    public HistoryMsgVo(Long sendTime, String context) {
        this.sendTime = sendTime;
        this.context = context;
    }

    private Long sendTime;

    private String context;
}
