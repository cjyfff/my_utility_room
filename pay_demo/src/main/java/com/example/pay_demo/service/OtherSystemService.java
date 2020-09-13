package com.example.pay_demo.service;

import org.springframework.stereotype.Service;

/**
 * 此处是模拟异步队列调用其他外部系统，例如优惠券，积分，送红包，发送短信
 *
 * Created by jiashen on 2020/9/7.
 */
@Service
public class OtherSystemService {

    /**
     * 在 MQ 中通知其它系统订单已支付成功
     * 插入队列出现异常时抛出异常
     * @param orderId
     */
    public void orderSuccessAction(String orderId) throws Exception {}

    /**
     * 在 MQ 中通知其它系统订单已撤销
     * 例如 优惠券撤销，积分加减
     * 插入队列出现异常时抛出异常
     * @param orderId
     * @throws Exception
     */
    public void orderCancelAction(String orderId) throws Exception {}
}
