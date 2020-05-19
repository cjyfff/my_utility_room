package com.example.hello.service.impl;


import java.util.concurrent.TimeUnit;

import com.example.hello.service.BService;
import com.example.hello.service.TestService;
import com.example.hello.vo.RespContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@Service(version = "1.0.0", timeout = 3000)
public class TestServiceImpl implements TestService {

    @Reference(version = "1.0.0", timeout = 3000)
    private BService bService;

    @Override
    public RespContent testDubbo() {
        try {
            System.out.println("hello test");
            //TimeUnit.SECONDS.sleep(10);

            bService.invokeB();

            RespContent resp = new RespContent();
            resp.setCode("000");
            resp.setMsg("ok!!!!");

            return resp;
        } catch (Exception e) {
            log.error("-", e);
            RespContent resp = new RespContent();
            resp.setCode("999");
            resp.setMsg(e.getMessage());
            return resp;
        }
    }
}
