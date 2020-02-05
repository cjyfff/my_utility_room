package com.example.hello.service;

/**
 * privider 和 consumer 的 service interface 接口包路径需一致，不然会报
 * No provider available for the service
 */
public interface TestService {
    void testDubbo();
}
