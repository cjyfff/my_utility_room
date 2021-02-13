package com.example.pay_demo.service;

import java.io.IOException;

import com.example.pay_demo.common.Lock.LockResult;
import com.example.pay_demo.vo.PayRespMsg;
import com.example.pay_demo.common.Lock;
import com.example.pay_demo.vo.payservice.PayServiceQueryResult;
import com.example.pay_demo.vo.payservice.PayServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2020/9/7.
 */
@Service
public class GeneralPayService {

    private final static Logger logger = LoggerFactory.getLogger(GeneralPayService.class);

    @Autowired
    private Lock lock;

    @Autowired
    private PayService payService;

    public PayRespMsg pay(String orderId) {
        LockResult lockResult = lock.tryLock(orderId);
        if (lockResult == null || ! lockResult.isSuccess()) {
            return new PayRespMsg("1001", "订单正在被处理");
        }

        boolean needCancel = true;

        PayRespMsg payRespMsg = new PayRespMsg("-9999", "system error");

        try {
            PayServiceResult payServiceResult  = payService.pay(orderId);
            BeanUtils.copyProperties(payServiceResult, payRespMsg);
            needCancel = false;
        } catch (IOException ioe) {
            logger.error("GeneralPayService get IOException: ", ioe);

            PayServiceQueryResult queryResult = payService.queryAndGetCancelCondition(orderId);
            // 假设 0001 代表渠道支付成功
            if (queryResult.isOrderSuccess()) {
                logger.info("询问渠道，订单已经支付成功");
                try {
                    payService.updateOrderPaySuccess(orderId);
                    logger.info("询问渠道，订单已经支付成功，已重新更新订单状态");
                    needCancel = false;
                    payRespMsg = new PayRespMsg("0000", "success");
                } catch (Exception e) {
                    logger.error("payService.updateOrderPaySuccess get error: ", e);
                    needCancel = true;
                    payRespMsg = new PayRespMsg("-9999", e.getMessage());
                }
            } else {
                needCancel = queryResult.isNeedCancel();
                payRespMsg = new PayRespMsg(queryResult.getCode(), queryResult.getMsg());
            }

        } catch (Exception e) {
            logger.error("GeneralPayService get error: ", e);
            needCancel = true;
            payRespMsg = new PayRespMsg("-9999", e.getMessage());
        } finally {
            if (needCancel) {
                payService.cancelByMq(orderId);
            }

            lock.tryUnLock(lockResult);
        }

        return payRespMsg;
    }
}
