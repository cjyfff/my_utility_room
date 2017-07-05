package com.example.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by cjyfff on 17-7-2.
 */
@Data
public class StockOperateLog {
    Integer id;
    Integer stockId;
    BigDecimal operateAmount;
    Date createAt;
    Date updateAt;
}
