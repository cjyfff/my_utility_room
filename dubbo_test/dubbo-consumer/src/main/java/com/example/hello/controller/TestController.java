package com.example.hello.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.example.hello.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Reference(version = "1.0.0", timeout = 3000)
    private TestService testService;

    @RequestMapping(path = "/test", method={RequestMethod.GET})
    public void test() {
        System.out.println("start invoke test method.");
        testService.testDubbo();
    }
}
