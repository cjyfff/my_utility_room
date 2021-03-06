package com.example.db_test.controller;

import com.example.db_test.mapper.LockTestMapper;
import com.example.db_test.service.LockTestService;
import com.example.db_test.service.TestService;
import com.example.db_test.service.TestService3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jiashen on 2020/7/22.
 */
@RestController
public class TestController {

    @Autowired
    private LockTestService testService;

    @RequestMapping(path = "/test", method = {RequestMethod.GET})
    public String test() {
        try {
            testService.test();
            return "ok";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
