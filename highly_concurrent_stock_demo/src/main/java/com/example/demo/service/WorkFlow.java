package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author jiashen
 * @date 17-7-5
 */
@Service
public class WorkFlow {
    private final WriteStockOperateLog writeStockOperateLog;

    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public WorkFlow(RedisTemplate<Object, Object> redisTemplate, WriteStockOperateLog writeStockOperateLog) {
        //    模拟一开始把数据库中的id，库存关系加载到缓存
        this.redisTemplate = redisTemplate;
        this.redisTemplate.opsForValue().set(1, BigDecimal.valueOf(1000000000));
        this.writeStockOperateLog = writeStockOperateLog;
        System.out.println("WorkFlow init completed.");
    }

    // todo：这样所有的库存扣减都会串行执行，有无办法优化？
    public synchronized void count(Integer stockId, BigDecimal operateAmount) throws Throwable {
            BigDecimal nowAmount = (BigDecimal) this.redisTemplate.opsForValue().get(stockId);
            BigDecimal newAmount = nowAmount.add(operateAmount);
            if (Integer.valueOf(1).equals(newAmount.compareTo(BigDecimal.valueOf(0)))) {
                this.redisTemplate.opsForValue().set(stockId, newAmount);
                writeStockOperateLog.writeStockOperateLog(stockId, operateAmount);
            } else {
                throw new Exception("库存id： " + stockId + ", 库存不足");
            }
    }

    public void getStock(Integer stockId) {
        System.out.println(this.redisTemplate.opsForValue().get(stockId));
    }
}
