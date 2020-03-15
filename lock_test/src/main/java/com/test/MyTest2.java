package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyTest2 {

    public static void transfer(Account from, Account to, int amount) throws InterruptedException {
        // 模拟正常的前置业务
        TimeUnit.SECONDS.sleep(1);
        Account maxAccount=from;
        Account minAccount=to;
        if(Long.parseLong(from.getCard())<Long.parseLong(to.getCard())){
            maxAccount=to;
            minAccount=from;
        }

        synchronized (maxAccount) {
            System.out.println(Thread.currentThread().getName() + " lock  account " + maxAccount.getCard());
            synchronized (minAccount) {
                System.out.println(Thread.currentThread().getName() + " lock  account " + minAccount.getCard());
                // 转出账号扣钱
                from.addMoney(-amount);
                // 转入账号加钱
                to.addMoney(amount);
            }
        }
        System.out.println("transfer success");
    }

    public static void main(String[] args) {
        Account from = new Account(100, "6000001");
        Account to = new Account(100, "6000002");

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        // 线程 1
            threadPool.execute(()->

        {
            try {
                transfer(from, to, 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 线程 2
            threadPool.execute(()->

        {
            try {
                transfer(to, from, 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}




