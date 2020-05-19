package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/helloNacos")
    public String helloNacos(){
        System.out.println("get request");
        return "你好，nacos！";
    }
}
