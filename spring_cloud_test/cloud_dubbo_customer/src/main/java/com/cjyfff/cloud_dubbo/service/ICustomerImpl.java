package com.cjyfff.cloud_dubbo.service;

import com.cjyfff.common.ofc.OneAuditOrderService;
import com.cjyfff.common.ofc.OrderAutoAuditHandlerParaVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2020/5/18.
 */
@Slf4j
@Service
public class ICustomerImpl implements ICustomer {
    @Reference
    private OneAuditOrderService oneAuditOrderService;

    @Override
    public String auditOrder() {
        log.info("Run CloudOrderAutoAuditHandler with paras: ");
        OrderAutoAuditHandlerParaVo reqVo = new OrderAutoAuditHandlerParaVo();
        reqVo.setOrderId("D45346546546");
        oneAuditOrderService.auditOrder(reqVo);

        return "okkk!";
    }
}
