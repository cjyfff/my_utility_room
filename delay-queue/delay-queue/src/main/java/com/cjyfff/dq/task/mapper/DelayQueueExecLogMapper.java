package com.cjyfff.dq.task.mapper;

import com.cjyfff.dq.task.model.DelayQueueExecLog;

public interface DelayQueueExecLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DelayQueueExecLog record);

    int insertSelective(DelayQueueExecLog record);

    DelayQueueExecLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DelayQueueExecLog record);

    int updateByPrimaryKey(DelayQueueExecLog record);
}