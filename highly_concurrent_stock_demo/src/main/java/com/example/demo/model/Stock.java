package com.example.demo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author cjyfff
 * @date 17-7-2
 */
@Data
public class Stock {
    Integer id;
    Integer userId;
    BigDecimal amount;
    Date createAt;
    Date updateAt;
}
