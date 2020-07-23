package com.cjyfff.cloud_dubbo.controller;

import com.alibaba.fastjson.JSON;

import com.cjyfff.cloud_dubbo.service.ICustomer;
import com.cjyfff.common.ofc.OrderAutoAuditHandlerParaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 2020/5/18.
 */
@RestController
public class TestController {
    @Autowired
    private ICustomer customer;

    @RequestMapping(path = "test", method={RequestMethod.GET})
    public String test() {

        return customer.auditOrder();
    }
}
