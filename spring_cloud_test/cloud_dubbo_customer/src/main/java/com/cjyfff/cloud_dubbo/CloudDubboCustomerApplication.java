package com.cjyfff.cloud_dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudDubboCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudDubboCustomerApplication.class, args);
    }

}
