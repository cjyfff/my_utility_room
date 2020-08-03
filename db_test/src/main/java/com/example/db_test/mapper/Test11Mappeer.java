package com.example.db_test.mapper;

import com.example.db_test.entity.Test11E;
import org.springframework.stereotype.Repository;

/**
 * Created by jiashen on 2020/7/22.
 */
@Repository
public interface Test11Mappeer {
    int insert(Test11E test11E);
}
