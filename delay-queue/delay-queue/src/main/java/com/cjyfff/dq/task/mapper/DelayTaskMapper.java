package com.cjyfff.dq.task.mapper;

import java.util.List;

import com.cjyfff.dq.task.model.DelayTask;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DelayTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DelayTask record);

    int insertSelective(DelayTask record);

    DelayTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DelayTask record);

    int updateStatusByOldStatusAndExecuteTime(@Param(value = "oldStatus") Integer oldStatus,
                                              @Param(value = "newStatus") Integer newStatus,
                                              @Param(value = "shardingId") Byte shardingId,
                                              @Param(value = "executeTimeBegin") Long executeTimeBegin,
                                              @Param(value = "executeTimeEnd") Long executeTimeEnd);

    int updateStatusByOldStatusAndTaskId(@Param(value = "oldStatus") Integer oldStatus,
                                         @Param(value = "newStatus") Integer newStatus,
                                         @Param(value = "shardingId") Byte shardingId,
                                         @Param(value = "taskId") String taskId);

    DelayTask selectByTaskId(@Param(value = "taskId") String taskId);


    List<DelayTask> selectByStatusAndExecuteTimeForUpdate(@Param(value = "oldStatus") Integer oldStatus,
                                                          @Param(value = "shardingId") Byte shardingId,
                                                          @Param(value = "executeTimeBegin") Long executeTimeBegin,
                                                          @Param(value = "executeTimeEnd") Long executeTimeEnd);

    int updateByPrimaryKey(DelayTask record);
}