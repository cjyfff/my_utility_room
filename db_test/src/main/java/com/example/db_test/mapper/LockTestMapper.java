package com.example.db_test.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by jiashen on 2020/11/17.
 */
@Repository
public interface LockTestMapper {
    int updateStatus(@Param(value = "id") Integer id, @Param(value = "name") String name,
                     @Param(value = "oldStatus") String oldStatus, @Param(value = "newStatus") String newStatus);
}
