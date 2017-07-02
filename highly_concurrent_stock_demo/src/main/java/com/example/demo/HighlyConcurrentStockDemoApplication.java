package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 高并发余额（库存）扣减方案：
 1、内存（缓存）中保存余额，操作数，对余额进行操作时，先进行操作，判断操作后的余额是否小于0,是的话就进行补偿操作。操作日志需要进行持久化。
    这个过程可以不加锁或者加内存锁。
 2、通过定时任务在每日闲时（例如晚上）根据操作数对数据库中的余额进行更新，更新成功后更新内存中的余额和操作数。
 */
@SpringBootApplication
public class HighlyConcurrentStockDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighlyConcurrentStockDemoApplication.class, args);
	}
}
