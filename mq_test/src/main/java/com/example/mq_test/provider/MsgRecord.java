package com.example.mq_test.provider;

import java.util.concurrent.ConcurrentHashMap;

import com.example.mq_test.vo.HistoryMsgVo;
import lombok.Getter;

/**
 * Created by jiashen on 2020/4/17.
 */
@Getter
public class MsgRecord {
    public static ConcurrentHashMap<String, HistoryMsgVo> HistoryMsgMap = new ConcurrentHashMap<>();
}
