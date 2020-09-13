package com.example.pay_demo.dao;

import com.example.pay_demo.domain.Order;
import org.springframework.stereotype.Repository;

/**
 * Created by jiashen on 2020/9/7.
 */
@Repository
public interface OrderMapper {

    Order getOrder(String orderId);

    int updateOrderByOrderId(Order order);

    int updateOrderStatus(String orderId, Integer orderStatus);

    int updateOrderCancelStatus(String orderId, Integer orderCancelStatus);
}
