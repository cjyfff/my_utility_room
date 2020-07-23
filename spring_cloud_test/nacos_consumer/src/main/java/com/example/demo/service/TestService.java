package com.example.demo.service;

import com.example.demo.controller.vo.DefaultWebApiResult;
import com.example.demo.controller.vo.OrderAutoAuditHandlerParaVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author cjyfff
 * @date 2020/5/23 17:43
 */
@FeignClient(value = "test-provider")
public interface TestService {

    @RequestMapping(method = RequestMethod.POST, value = "/auditOrder")
    DefaultWebApiResult auditOrder(@RequestBody OrderAutoAuditHandlerParaVo reqVo);
}
