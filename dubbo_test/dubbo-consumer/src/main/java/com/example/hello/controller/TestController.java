package com.example.hello.controller;


import com.example.hello.service.TestService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Reference(version = "1.0.0", timeout = 3000)
    private TestService testService;

    @RequestMapping(path = "/test", method={RequestMethod.GET})
    public String test() {
        System.out.println("start invoke test method.");
        return testService.testDubbo().getMsg();
    }
}
