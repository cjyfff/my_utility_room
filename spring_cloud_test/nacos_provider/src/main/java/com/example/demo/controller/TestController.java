package com.example.demo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.demo.controller.vo.DefaultWebApiResult;
import com.example.demo.controller.vo.OrderAutoAuditHandlerParaVo;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @GetMapping("/helloNacos")
    public String helloNacos(){
        System.out.println("get request");
        return "你好，nacos！";
    }

    @SentinelResource(value="auditOrder")
    @RequestMapping(path = "/auditOrder", method={RequestMethod.POST})
    public DefaultWebApiResult auditOrder(@RequestBody OrderAutoAuditHandlerParaVo reqDto) {
        System.out.println("处理订单自动客审数据:" + reqDto.getOrderId());
        // 模拟响应超时，sentinel服务降级
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DefaultWebApiResult.success();
    }
}
