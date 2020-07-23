package com.example.db_test.service;

import java.util.Date;

import com.example.db_test.entity.Test11E;
import com.example.db_test.mapper.Test11Mappeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2020/7/22.
 */
@Service
public class TestService {

    @Autowired
    private Test11Mappeer test11Mappeer;

    @Autowired
    private TestService2 testService2;

    @Transactional
    public void test() {



        try {

            testService2.test2();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Test11E t1 = new Test11E();
        t1.setId(4);
        //t1.setT(new Date());

        test11Mappeer.insert(t1);
    }
}
