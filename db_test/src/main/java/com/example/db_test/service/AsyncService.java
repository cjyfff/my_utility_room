package com.example.db_test.service;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2020/11/17.
 */
@Service
public class AsyncService {

    @Async
    public void asyncM() throws Exception {
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {

        }
        int a = 1 / 0;
    }
}
