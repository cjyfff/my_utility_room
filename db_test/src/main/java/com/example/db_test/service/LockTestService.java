package com.example.db_test.service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试数据库乐观锁
 * Created by jiashen on 2020/11/17.
 */
@Service
public class LockTestService {

    @Autowired
    private LockTestTransService lockTestTransService;

    public void test() throws Exception {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("lock-test-pool-%d").build();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 10000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(20),
            namedThreadFactory,
            (r, executor) -> {
                try {
                    if (!executor.isShutdown()) {
                        executor.getQueue().put(r);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

        // 使用非线程安全的类，进行非线程安全的操作
        final Integer[] a = {0};
        AtomicInteger standardCount = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            pool.submit(() -> {
                for (int j = 0; j < 100; j++) {
                    try {

                        lockTestTransService.doTest(a, standardCount);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        pool.shutdown();

        while (! pool.isTerminated()) {
            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("res: " + a[0].equals(standardCount.get()));
    }
}
