package com.example.demo.service;

import com.example.demo.mapper.StockMapper;
import com.example.demo.mapper.StockOperateLogMapper;
import com.example.demo.model.Stock;
import com.example.demo.model.StockOperateLog;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 17-7-5.
 */
@Service
public class WorkFlow {
    public WorkFlow(StockMapper stockMapper, StockOperateLogMapper stockOperateLogMapper) {
        Stock stock = stockMapper.selectStockById(1);
        StockOperateLog stockOperateLog = stockOperateLogMapper.selectStockOpLogById(1);
        System.out.println(stock);
    }
}
