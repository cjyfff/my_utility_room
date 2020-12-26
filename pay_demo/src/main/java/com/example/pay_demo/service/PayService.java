package com.example.pay_demo.service;

import java.io.IOException;

import com.example.pay_demo.common.enumm.OrderCancelStatus;
import com.example.pay_demo.common.enumm.OrderStatus;
import com.example.pay_demo.dao.OrderMapper;
import com.example.pay_demo.domain.Order;
import com.example.pay_demo.vo.channel.ChannelQueryResult;
import com.example.pay_demo.vo.channel.ChannelResp;
import com.example.pay_demo.vo.payservice.PayServiceQueryResult;
import com.example.pay_demo.vo.payservice.PayServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2020/9/7.
 */
@Service
public class PayService {

    private final static Logger logger = LoggerFactory.getLogger(PayService.class);

    @Autowired
    private ChannelService channelService;

    @Autowired
    private OtherSystemService otherSystemService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 支付接口
     * 因为外层没有事务，所以这里可以不用加 REQUIRES_NEW
     * 不过为了避免外层误加了@Transactional的情况，这里保险起见还是加上
     * 下面的方法同理
     * @param orderId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public PayServiceResult pay(String orderId) throws Exception, IOException {
        // 乐观锁再进行一次加锁，以防 redis 不可靠的情况
        if (orderMapper.updateOrderWithStatus(
            orderId, OrderStatus.INIT.getStatus(), OrderStatus.CHANNEL_PAYING.getStatus()) <= 0) {
            return new PayServiceResult("1002", "订单不是初始化状态");
        }

        Order order = orderMapper.getOrder(orderId);

        // 调用渠道接口，出现超时等IOException会向外抛出异常
        ChannelResp channelResp = channelService.pay(orderId);

        if (channelResp == null || ! "0000".equals(channelResp.getCode())) {
            order.setStatus(OrderStatus.FAIL.getStatus());
            orderMapper.updateOrderByOrderId(order);
            if (channelResp == null) {
                return new PayServiceResult("1003", "渠道返回空结果");
            } else {
                return new PayServiceResult(channelResp.getCode(), channelResp.getMsg());
            }
        }

        //支付成功通知其它系統(MQ)
        otherSystemService.orderSuccessAction(orderId);

        // 更新数据库
        order.setStatus(OrderStatus.SUCCESS.getStatus());
        orderMapper.updateOrderByOrderId(order);

        return new PayServiceResult("0000", "success");

    }

    /**
     * 查询渠道的订单情况，在渠道确认支付成功时操作
     * @param orderId
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateOrderPaySuccess(String orderId) throws Exception {
        //支付成功通知其它系統(MQ)

        otherSystemService.orderSuccessAction(orderId);

        // 更新数据库
        orderMapper.updateOrderStatus(orderId, OrderStatus.SUCCESS.getStatus());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void cancel(String orderId) {
        try {
            ChannelResp channelResp = channelService.cancel(orderId);

            if (channelResp == null) {
                logger.error("渠道撤销接口响应为空");
                return;
            }

            if (! "0000".equals(channelResp.getCode())) {
                logger.error("渠道撤销接口响应失败, code: {}, msg: {}", channelResp.getCode(), channelResp.getMsg());
                return;
            }

            otherSystemService.orderCancelAction(orderId);
            orderMapper.updateOrderCancelStatus(orderId, OrderCancelStatus.CANCELED.getStatus());

        } catch (Exception e) {
            logger.error("PayService cancel method get error: ", e);
        }
    }

    public PayServiceQueryResult queryAndGetCancelCondition(String orderId) {
        ChannelQueryResult channelQueryResult = channelService.queryAndGetCancelCondition(orderId);
        PayServiceQueryResult result = new PayServiceQueryResult();
        BeanUtils.copyProperties(channelQueryResult, result);
        return result;
    }
}
