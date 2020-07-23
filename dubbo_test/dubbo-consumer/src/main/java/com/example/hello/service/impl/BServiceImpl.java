package com.example.hello.service.impl;

import com.example.hello.service.BService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * Created by jiashen on 2020/5/19.
 */
@Slf4j
@Component
@Service(version = "1.0.0", timeout = 3000)
public class BServiceImpl implements BService {
    @Override
    public void invokeB() {
        log.info("invokeB...");
    }
}
