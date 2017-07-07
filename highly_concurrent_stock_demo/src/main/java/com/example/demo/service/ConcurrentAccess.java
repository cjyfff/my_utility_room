package com.example.demo.service;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author jiashen
 * @date 17-7-6
 */
@Service
public class ConcurrentAccess {

    public ConcurrentAccess(WorkFlow workFlow) {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
        ExecutorService es = new ThreadPoolExecutor(10, 100, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 100000; i++) {
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
                    workFlow.getStock(1);
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



