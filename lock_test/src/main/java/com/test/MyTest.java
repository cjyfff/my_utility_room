package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class MyTest {

    private static void transfer(Account from, Account to, int amount) throws InterruptedException {
        // 模拟正常的前置业务
        TimeUnit.SECONDS.sleep(1);
        // 保证转账一定成功
        while (true) {
            if (from.lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " lock from account " + from.getCard());
                    if (to.lock.tryLock(1, TimeUnit.SECONDS)) {
                        try {
                            System.out.println(Thread.currentThread().getName() + " lock to account " + to.getCard());
                            // 转出账号扣钱
                            from.addMoney(-amount);
                            // 转入账号加钱
                            to.addMoney(amount);
                            break;
                        } finally {
                            to.lock.unlock();
                        }

                    }
                } finally {
                    from.lock.unlock();
                }
            }
        }
        System.out.println("transfer success");

    }

    public static void main(String[] args) {
        Account from = new Account(100, "A");
        Account to = new Account(100, "B");

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        // 线程 1
        threadPool.execute(() -> {
            try {
                transfer(from, to, 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 线程 2
        threadPool.execute(() -> {
            try {
                transfer(to, from, 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}



class Account {
    public Account(int balance, String card) {
        this.balance = balance;
        this.card = card;
    }
    private int balance;

    private String card;

    public ReentrantLock lock = new ReentrantLock();

    public void addMoney(int amount) {
        balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
