package com.example.demo.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.example.demo.mapper.StockMapper;
import com.example.demo.mapper.StockOperateLogMapper;
import com.example.demo.model.Stock;
import com.example.demo.model.StockOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jiashen
 * @date 17-7-5
 */
@Service
public class WorkFlow {
    @Autowired
    WriteStockOperateLog writeStockOperateLog;

    // id与库存的对应关系
    private Map<Integer, BigDecimal> stockMap;

    public WorkFlow() {
    //    模拟一开始把数据库中的id，库存关系加载到缓存
        stockMap = new HashMap<Integer, BigDecimal>() {
            {
                put(1, BigDecimal.valueOf(1000000000));
            }
        };
        System.out.println("WorkFlow init completed.");
    }

    public synchronized void count(Integer stockId, BigDecimal operateAmount) throws Throwable {
            BigDecimal nowAmount = stockMap.get(stockId);
            BigDecimal newAmount = nowAmount.add(operateAmount);
            if (Integer.valueOf(1).equals(newAmount.compareTo(BigDecimal.valueOf(0)))) {
                stockMap.put(stockId, newAmount);
                writeStockOperateLog.writeStockOperateLog(stockId, operateAmount);
            } else {
                throw new Exception("库存id： " + stockId + ", 库存不足");
            }
    }

    public void getStock() {
        System.out.println(stockMap);
    }
}
