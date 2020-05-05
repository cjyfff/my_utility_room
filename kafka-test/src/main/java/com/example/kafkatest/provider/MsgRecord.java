package com.example.kafkatest.provider;

import java.util.concurrent.ConcurrentHashMap;

import com.example.kafkatest.vo.HistoryMsgVo;
import lombok.Getter;

/**
 * Created by jiashen on 2020/4/23.
 */
@Getter
public class MsgRecord {
    public static ConcurrentHashMap<String, HistoryMsgVo> HistoryMsgMap = new ConcurrentHashMap<>();
}
