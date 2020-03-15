package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock {
    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private static ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    private static void read() {
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " start to read, got read lock");
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread().getName() + " read finished, release read lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    private static void write() {
        writeLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " start to write, got write lock");
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread().getName() + " read finished, release write lock");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 2; i++) {
            executorService.execute(ReadWriteLock::write);
        }
        for (int i = 0; i < 5; i++) {
            executorService.execute(ReadWriteLock::read);
        }

    }
}
