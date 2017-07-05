package com.example.demo.mapper;

import com.example.demo.model.StockOperateLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by cjyfff on 17-7-2.
 */
@Mapper
public interface StockOperateLogMapper {
    StockOperateLog selectStockOpLogById(Integer id);

    Integer insertStockOperateLog(StockOperateLog stockOperateLog);
}
