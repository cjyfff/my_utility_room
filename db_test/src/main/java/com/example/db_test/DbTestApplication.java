package com.example.db_test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@MapperScan("com.example.db_test.mapper")
@SpringBootApplication
public class DbTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbTestApplication.class, args);
    }

}
