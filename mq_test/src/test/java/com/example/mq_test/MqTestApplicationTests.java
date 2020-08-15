package com.example.mq_test;

import com.example.mq_test.provider.Provider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MqTestApplicationTests {

    @Autowired
    private Provider provider;

    @Test
    void contextLoads() {
    }

    //@Test
    //public void hello() throws Exception {
    //    provider.send();
    //}

}
