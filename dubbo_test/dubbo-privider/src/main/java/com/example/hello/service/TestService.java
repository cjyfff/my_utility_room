package com.example.hello.service;

import com.example.hello.vo.RespContent;

/**
 * privider 和 consumer 的 service interface 接口包路径需一致，不然会报
 * No provider available for the service
 */
public interface TestService {
    RespContent testDubbo();
}
