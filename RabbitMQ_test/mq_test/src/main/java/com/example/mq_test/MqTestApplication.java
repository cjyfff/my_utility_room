package com.example.mq_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MqTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqTestApplication.class, args);
    }

}
