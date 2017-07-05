package com.example.demo.mapper;

import com.example.demo.model.Stock;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by cjyfff on 17-7-2.
 */
@Mapper
public interface StockMapper {
    Stock selectStockById(Integer id);

    Integer updateStockByPrimaryKeySelective(Stock stock);
}
