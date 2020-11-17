package com.example.db_test.service;

import java.util.Date;

import com.example.db_test.entity.Test11E;
import com.example.db_test.mapper.Test11Mappeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jiashen on 2020/11/17.
 */
@Service
public class TestService3 {
    @Autowired
    private Test11Mappeer test11Mappeer;

    @Autowired
    private AsyncService asyncService;

    /**
     * async里的异常不会导致主线程回滚
     * @throws Exception
     */
    @Transactional
    public void test() throws Exception {
        Test11E t2 = new Test11E();
        t2.setId(10);
        t2.setT(new Date());

        test11Mappeer.insert(t2);

        asyncService.asyncM();
    }
}
