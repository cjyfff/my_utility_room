package com.example.demo.controller;

import com.example.demo.controller.vo.DefaultWebApiResult;
import com.example.demo.controller.vo.OrderAutoAuditHandlerParaVo;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @GetMapping("/helloNacos")
    public String helloNacos(){
        System.out.println("get request");
        return "你好，nacos！";
    }

    @RequestMapping(path = "/auditOrder", method={RequestMethod.POST})
    public DefaultWebApiResult auditOrder(@RequestBody OrderAutoAuditHandlerParaVo reqDto) {
        System.out.println("处理订单自动客审数据:" + reqDto.getOrderId());
        return DefaultWebApiResult.success();
    }
}
