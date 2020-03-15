package com.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {
    private static AtomicReference<Thread> sign = new AtomicReference<>();

    private static void lock() {
        Thread current = Thread.currentThread();
        while (!sign.compareAndSet(null, current)) {
            System.out.println("fail to set!");
        }
    }

    private static void unlock() {
        Thread thread = Thread.currentThread();
        sign.compareAndSet(thread, null);
    }

    public static void main(String[] args) {
        Runnable runnable = () -> {
            System.out.println("start to get lock");
            SpinLock.lock();
            System.out.println("got lock successfully!");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                SpinLock.unlock();
            }
        };

        Thread thread = new Thread(runnable);
        Thread thread1 = new Thread(runnable);

        thread.start();
        thread1.start();
    }

}
