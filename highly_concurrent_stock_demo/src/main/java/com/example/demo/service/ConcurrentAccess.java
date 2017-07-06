package com.example.demo.service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 17-7-6.
 */
@Service
public class ConcurrentAccess {

    public ConcurrentAccess(WorkFlow workFlow) {
        ExecutorService es = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 1000; i++) {
            es.submit(() -> {
                try {
                    workFlow.count(1, BigDecimal.valueOf(-1));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            });
        }
        es.shutdown();

        try {
            while (true) {
                if (es.isTerminated()) {
                    workFlow.getStock();
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println();
    }
}



