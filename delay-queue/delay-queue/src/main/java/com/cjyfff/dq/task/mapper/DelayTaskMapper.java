package com.cjyfff.dq.task.mapper;

import com.cjyfff.dq.task.model.DelayTask;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DelayTask record);

    int insertSelective(DelayTask record);

    DelayTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DelayTask record);

    int updateByPrimaryKey(DelayTask record);
}