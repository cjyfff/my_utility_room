package com.example.db_test.service;

import java.util.concurrent.atomic.AtomicInteger;

import com.example.db_test.mapper.LockTestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2020/11/17.
 */
@Service
public class LockTestTransService {

    @Autowired
    private LockTestMapper lockTestMapper;

    @Transactional(rollbackFor = Exception.class)
    public void doTest(Integer[] a, AtomicInteger standardCount){
        if (lockTestMapper.updateStatus(1, "a", "0", "1") > 0) {
            a[0]++;
            standardCount.getAndIncrement();
            lockTestMapper.updateStatus(1, "a", "1", "0");
        }
    }
}
