package com.example.db_test.service;

import java.util.Date;

import com.example.db_test.entity.Test11E;
import com.example.db_test.mapper.Test11Mappeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2020/7/22.
 */
@Service
public class TestService2 {
    @Autowired
    private Test11Mappeer test11Mappeer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void test2() {
        Test11E t2 = new Test11E();
        t2.setId(5);
        t2.setT(new Date());

        test11Mappeer.insert(t2);
    }
}
