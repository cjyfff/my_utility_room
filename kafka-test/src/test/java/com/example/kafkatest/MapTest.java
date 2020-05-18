package com.example.kafkatest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.example.kafkatest.provider.MsgRecord;
import com.example.kafkatest.vo.HistoryMsgVo;

/**
 * Created by jiashen on 2020/4/23.
 */
public class MapTest {
    public static void main(String[] args) {
        int cap = 10000;

        Map<Integer, String> map = new HashMap<>(2 * cap);

        for (int i = 0; i < cap; i++) {
            map.put(i, "haha");
        }

        Set<Entry<Integer, String>> set = map.entrySet();
        set.removeIf(record -> record.getKey() % 2 == 0);

    }
}
