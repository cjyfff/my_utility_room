package com.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class LockTest {
    private CuratorFramework client;

    @Before
    public void initMethod() {
        CuratorFramework c = CuratorFrameworkFactory.newClient("192.168.1.17:2181", 30000,
                10000, new ExponentialBackoffRetry(1000, 3));
        c.start();
        this.client = c;
    }


    @Test
    public void testLock() {
        InterProcessMutex lock1 = new InterProcessMutex(client,"/lock");
        InterProcessMutex lock2 = new InterProcessMutex(client,"/lock");

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("t1 start");
                lock1.acquire();
                TimeUnit.SECONDS.sleep(60);
                lock1.release();
                System.out.println("t1 end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        Thread t2 = new Thread(() -> {
            try {
                System.out.println("t2 start");
                lock1.acquire();
                TimeUnit.SECONDS.sleep(60);
                lock1.release();
                System.out.println("t2 end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void afterMethod() {
        client.close();
    }
}
