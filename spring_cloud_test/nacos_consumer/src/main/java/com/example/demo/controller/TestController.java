package com.example.demo.controller;

import com.example.demo.controller.vo.DefaultWebApiResult;
import com.example.demo.controller.vo.OrderAutoAuditHandlerParaVo;
import com.example.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Autowired
    private TestService testService;

    @GetMapping("/consumer")
    public String test1() {
        return restTemplate.getForObject("http://test-provider/helloNacos",String.class);
    }

    @GetMapping("/auditOrder")
    public String auditOrder() {
        OrderAutoAuditHandlerParaVo reqVo = new OrderAutoAuditHandlerParaVo();
        reqVo.setOrderId("D1323434");
        DefaultWebApiResult result = testService.auditOrder(reqVo);
        return result.getCode();
    }
}
