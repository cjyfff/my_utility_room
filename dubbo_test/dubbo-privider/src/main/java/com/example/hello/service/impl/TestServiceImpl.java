package com.example.hello.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.example.hello.service.TestService;
import org.springframework.stereotype.Component;

@Component
@Service(version = "1.0.0", timeout = 3000)
public class TestServiceImpl implements TestService {

    @Override
    public void testDubbo() {
        System.out.println("hello test");

    }
}
