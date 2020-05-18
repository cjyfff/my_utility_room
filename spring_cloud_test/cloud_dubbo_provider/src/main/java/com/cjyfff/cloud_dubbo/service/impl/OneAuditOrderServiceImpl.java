package com.cjyfff.cloud_dubbo.service.impl;

import com.cjyfff.common.ofc.OneAuditOrderService;
import com.cjyfff.common.ofc.OrderAutoAuditHandlerParaVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

/**
 * Created by jiashen on 2020/5/18.
 */
@Slf4j
@Service(version = "1.0.0", protocol = "dubbo")
public class OneAuditOrderServiceImpl implements OneAuditOrderService {

    @Override
    public String auditOrder(OrderAutoAuditHandlerParaVo reqVo) {
        log.info("处理订单自动客审数据：{}", reqVo.getOrderId());
        // todo: 补全逻辑

        return "ok";
    }
}
